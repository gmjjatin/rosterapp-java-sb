package com.hilabs.rapipeline.ingestion;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.dto.ErrorDetails;
import com.hilabs.rapipeline.dto.RAFileMetaData;
import com.hilabs.rapipeline.model.FileMetaDataTableStatus;
import com.hilabs.rapipeline.service.*;
import com.hilabs.roster.entity.RAPlmRoFileData;
import com.hilabs.roster.entity.RAProvDetails;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.hilabs.rapipeline.model.FileMetaDataTableStatus.*;
import static com.hilabs.rapipeline.util.Utils.trimToNChars;
import static com.hilabs.roster.util.Constants.*;
//import static com.hilabs.roster.util.Constants.*;

@Slf4j
//TODO what if same file is placed again???
//TODO handle Reject files not in 6 states/GBD markets/white glove providers??
public class IngestionTask extends Task {
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;
    private FileSystemUtilService fileSystemUtilService;
    private RAFileDetailsService raFileDetailsService;
    private RAProviderService raProviderService;
    private RAErrorLogsService raErrorLogsService;
    private IngestionValidationService ingestionValidationService;

    //TODO this approach won't work for multiple files.
    public static ConcurrentHashMap<Long, String> runningMap = new ConcurrentHashMap<>();

    private static final Gson gson = new Gson();

    public IngestionTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.fileSystemUtilService = (FileSystemUtilService) applicationContext.getBean("fileSystemUtilService");
        this.raFileMetaDataDetailsService = (RAFileMetaDataDetailsService) applicationContext.getBean("RAFileMetaDataDetailsService");
        this.raFileDetailsService = (RAFileDetailsService) applicationContext.getBean("RAFileDetailsService");
        this.raProviderService = (RAProviderService) applicationContext.getBean("RAProviderService");
        this.raErrorLogsService = (RAErrorLogsService) applicationContext.getBean("RAErrorLogsService");
        this.ingestionValidationService = (IngestionValidationService) applicationContext.getBean("ingestionValidationService");
    }

    public boolean shouldRun(RAFileMetaData raFileMetaData) {
        //TODO confirm
        Long plmRoFileDataId = raFileMetaData.getRaPlmRoFileDataId();
        if (runningMap.containsKey(plmRoFileDataId)) {
            log.warn("Ingestion task in progress for raFileMetaData {}", gson.toJson(raFileMetaData));
            return false;
        }
        return isShouldReprocess(raFileMetaData);
    }

    public boolean isShouldReprocess(RAFileMetaData raFileMetaData) {
        Optional<RAPlmRoFileData> raPlmRoFileDataOptional = raFileMetaDataDetailsService
                .findById(raFileMetaData.getRaPlmRoFileDataId());
        if (raPlmRoFileDataOptional.isPresent()) {
            RAPlmRoFileData raPlmRoFileData = raPlmRoFileDataOptional.get();
            if (raPlmRoFileData.getRaFileProcessingStatus() != null && raPlmRoFileData.getRaFileProcessingStatus().equalsIgnoreCase(NEW.name())) {
                return true;
            }
            return raPlmRoFileData.getReProcess() != null && raPlmRoFileData.getReProcess().toUpperCase().startsWith("Y");
        }
        return false;
    }

    public RAFileMetaData getRAFileMetaDataFromTaskData() {
        Map<String, Object> taskData = getTaskData();
        //Core framework level errors should change anything related to file
        if (!taskData.containsKey("data")) {
            log.warn("taskData doesn't have key data - taskData {}", gson.toJson(taskData));
            return null;
        }
        if (!(taskData.get("data") instanceof RAFileMetaData)) {
            log.warn("data field in taskData doesn't have valid data - data {} taskData {}", gson.toJson(taskData.get("data")),
                    gson.toJson(taskData));
            return null;
        }
        return (RAFileMetaData) taskData.get("data");
    }

    @Override
    public void run() {
        log.info("IngestionTask stared for {}", gson.toJson(getTaskData()));
        RAFileMetaData raFileMetaData = getRAFileMetaDataFromTaskData();
        if (raFileMetaData == null) {
            return;
        }
        String fileName = raFileMetaData.getFileName();
        if (fileName == null) {
            log.warn("raFileMetaData {} has these no file name", gson.toJson(raFileMetaData));
            //TODO later handle
            return;
        }
        try {
            if (!shouldRun(raFileMetaData)) {
                return;
            }
            runningMap.put(raFileMetaData.getRaPlmRoFileDataId(), fileName);
            //Step 1 - Meta data validation
            ErrorDetails validationErrorDetails = ingestionValidationService.validateMetaDataAndGetErrorList(raFileMetaData);
            if (validationErrorDetails != null) {
                log.warn("raFileMetaDataDetails {} has these errors {} in metadata",
                        gson.toJson(raFileMetaData), gson.toJson(validationErrorDetails));
                Optional<RAProvDetails> optionalRAProvDetails = raProviderService.findByProvider(raFileMetaData.getOrgName());
                //TODO fix status code
                upsertIngestionStatus(raFileMetaData, REJECTED, ROSTER_INGESTION_VALIDATION_FAILED,
                        optionalRAProvDetails.<Long>map(RAProvDetails::getId).orElse(null),null,
                        validationErrorDetails, false);
                return;
            }

            //Already checked in validateMetaDataAndGetErrorList
            Optional<RAProvDetails> optionalRAProvDetails = raProviderService.findByProvider(raFileMetaData.getOrgName());
            RAProvDetails raProvDetails = optionalRAProvDetails.get();
            //Step 2 - File validation
            ErrorDetails validateFileErrorDetails = validateFile(raFileMetaData);
            if (validateFileErrorDetails != null) {
                upsertIngestionStatus(raFileMetaData, REJECTED, ROSTER_INGESTION_VALIDATION_FAILED,
                        optionalRAProvDetails.<Long>map(RAProvDetails::getId).orElse(null), null,
                        validateFileErrorDetails, false);
                return;
            }


            String standardizedFileName = getStandardizedFileName(raFileMetaData);
            String sourceFilePath = fileSystemUtilService.getSourceFilePath(fileName);
            String destinationFilePath = fileSystemUtilService.getDestinationFilePath(standardizedFileName);
            String archiveFilePath = fileSystemUtilService.getArchiveFilePath(fileName);
            //TODO fix password. Confirm if it is always null
            String password = null;

//
            //Copy file to destination
            if (!copyToDestAndArchive(sourceFilePath, destinationFilePath, archiveFilePath, password)) {
                String errorDescription = String.format("Copying files failed for raFileMetaDataDetails %s",
                        gson.toJson(raFileMetaData));
                log.warn(errorDescription);
                //TODO fix status code
                upsertIngestionStatus(raFileMetaData, REJECTED, ROSTER_INGESTION_FAILED,
                        optionalRAProvDetails.<Long>map(RAProvDetails::getId).orElse(null),
                        standardizedFileName,
                        new ErrorDetails("System error for copying files", errorDescription), false);
                return;
            }
            upsertIngestionStatus(raFileMetaData, IN_PROGRESS, ROSTER_INGESTION_COMPLETED, raProvDetails.getId(),
                    standardizedFileName, null, false);
            if (!deleteFileIfExists(sourceFilePath)) {
                //TODO later do we need to stop process here???
                log.warn("Error deleting sourceFilePath {} - raFileMetaDataDetails {}", sourceFilePath, gson.toJson(raFileMetaData));
            }
            log.debug("IngestionTask done for {}", gson.toJson(getTaskData()));
        } catch (Exception ex) {
            log.error("Error in IngestionTask raFileMetaData {} ex {}", gson.toJson(raFileMetaData),
                    ex.getMessage());
            //TODO fix it
            String stacktrace = trimToNChars(ExceptionUtils.getStackTrace(ex), 1000);
            String message = trimToNChars(ex.getMessage(), 1000);
            upsertIngestionStatus(raFileMetaData, FileMetaDataTableStatus.valueOf(raFileMetaData.getRAFileProcessingStatus()),
                    ROSTER_INGESTION_FAILED,
                    null, null, new ErrorDetails(message, stacktrace),
                    raFileMetaData.getReprocess() != null && raFileMetaData.getReprocess().toUpperCase().startsWith("Y"));
        } finally {
            runningMap.remove(raFileMetaData.getRaPlmRoFileDataId());
        }
    }

    public boolean deleteFileIfExists(String sourceFilePath) {
        try {
            Files.deleteIfExists(Paths.get(sourceFilePath));
            return true;
        } catch (IOException ex) {
            log.error("IOException deleting sourceFilePath {} - ex {}", sourceFilePath, ex.getMessage());
            return false;
        }
    }

    public String getStandardizedFileName(RAFileMetaData raFileMetaData) {
        //TODO fix plm ticket id
        String plmTicketId = raFileMetaData.getRoId();
        String dcnId = raFileMetaData.getDcnId();
        //TODO is it created or deposited date??
        Date createdDate = raFileMetaData.getDepositDate();
        SimpleDateFormat formatter = new SimpleDateFormat("YYYYMMDDHHmmss");
        String strDate = formatter.format(createdDate);
        return plmTicketId + "-" + dcnId + "-" + strDate + ".xlsx";
    }

    public ErrorDetails validateFile(RAFileMetaData raFileMetaData) {
        String sourceFilePath = fileSystemUtilService.getSourceFilePath(raFileMetaData.getFileName());
        File file = new File(sourceFilePath);
        if (!file.exists()) {
            log.warn("File with name {} doesn't exists - raFileMetaDataDetails {}", raFileMetaData.getFileName(), gson.toJson(raFileMetaData));
            return new ErrorDetails("File missing", String.format("File with name %s doesn't exists",
                    raFileMetaData.getFileName()));
        }
        return null;
    }

    public boolean copyToDestAndArchive(String sourceFilePath, String destinationFilePath, String archiveFilePath, String password) {
        boolean copied = true;
        if (password == null) {
            copied = fileSystemUtilService.copyFileToDest(sourceFilePath, destinationFilePath);
            copied = copied & fileSystemUtilService.copyFileToDest(sourceFilePath, archiveFilePath);
        } else {
            copied = fileSystemUtilService.copyPasswordProtectedXlsxFileToDest(sourceFilePath, destinationFilePath, password);
            copied = copied & fileSystemUtilService.copyPasswordProtectedXlsxFileToDest(sourceFilePath, archiveFilePath, password);
        }
        return copied;
    }

    public void upsertIngestionStatus(RAFileMetaData raFileMetaData, FileMetaDataTableStatus status, int statusCode,
                                      Long raProvDetailsId, String standardizedFileName, ErrorDetails errorDetails,
                                      boolean reProcess) {
        String fileName = raFileMetaData.getFileName();
        //TODO insert ticket ids
        String market = raFileMetaData.getCntState();
        String lob = raFileMetaData.getPlmNetwork();
        Long raFileDetailsId = raFileDetailsService.insertRAFileDetails(raProvDetailsId, fileName,
                standardizedFileName, market, statusCode, null, null);
        raFileMetaDataDetailsService.insertRAFileDetailsLob(raFileDetailsId, lob, 1);
        if (raFileMetaData.getDcnId() != null) {
            raFileMetaDataDetailsService.insertRARTFileAltIds(raFileDetailsId, raFileMetaData.getDcnId(), "DCN_ID", 1);
        }
        if (raFileMetaData.getRoId() != null) {
            raFileMetaDataDetailsService.insertRARTFileAltIds(raFileDetailsId, raFileMetaData.getRoId(), "RO_ID", 1);
        }
        //TODO need to get contact from file
//        raFileMetaDataDetailsService.insertRARTContactDetails(raFileDetailsId, null,
//                contact, contactType, isActive)
        raFileMetaDataDetailsService.updateRAPlmRoFileDataStatus(raFileMetaData, status, reProcess);
        if (errorDetails != null) {
            //TODO what should be errorCodeDetailsId
            raErrorLogsService.insertRASystemErrors(raFileDetailsId, null, "INGESTION",
                    null, errorDetails.getErrorDescription(),
                    errorDetails.getErrorLongDescription());
        }
    }
}