package com.hilabs.rapipeline.ingestion;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.dto.ErrorDetails;
import com.hilabs.rapipeline.dto.RAFileMetaData;
import com.hilabs.rapipeline.model.FileMetaDataTableStatus;
import com.hilabs.rapipeline.service.*;
import com.hilabs.roster.dto.AltIdType;
import com.hilabs.roster.service.DartRASystemErrorsService;
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

import static com.hilabs.rapipeline.model.FileMetaDataTableStatus.IN_PROGRESS;
import static com.hilabs.rapipeline.model.FileMetaDataTableStatus.REJECTED;
import static com.hilabs.rapipeline.service.IngestionTaskService.ingestionTaskRunningMap;
import static com.hilabs.rapipeline.util.Utils.trimToNChars;
import static com.hilabs.roster.util.Constants.*;

@Slf4j
//TODO what if same file is placed again???
//TODO handle Reject files not in 6 states/GBD markets/white glove providers??
public class IngestionTask extends Task {
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;
    private FileSystemUtilService fileSystemUtilService;
    private RAFileDetailsService raFileDetailsService;
    private RAErrorLogsService raErrorLogsService;
    private IngestionTaskService ingestionTaskService;

    private DartRASystemErrorsService dartRASystemErrorsService;

    //TODO this approach won't work for multiple files.

    private static final Gson gson = new Gson();

    public IngestionTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.fileSystemUtilService = (FileSystemUtilService) applicationContext.getBean("fileSystemUtilService");
        this.raFileMetaDataDetailsService = (RAFileMetaDataDetailsService) applicationContext.getBean("RAFileMetaDataDetailsService");
        this.raFileDetailsService = (RAFileDetailsService) applicationContext.getBean("RAFileDetailsService");
        this.raErrorLogsService = (RAErrorLogsService) applicationContext.getBean("RAErrorLogsService");
        this.ingestionTaskService = (IngestionTaskService) applicationContext.getBean("ingestionTaskService");
        this.dartRASystemErrorsService = (DartRASystemErrorsService) applicationContext.getBean("dartRASystemErrorsService");
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
        log.info("IngestionTask started for {}", gson.toJson(getTaskData()));
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
//            if (!ingestionTaskService.shouldRun(raFileMetaData)) {
//                return;
//            }
            ingestionTaskRunningMap.put(raFileMetaData.getRaPlmRoFileDataId(), fileName);

            //Already checked in validateMetaDataAndGetErrorList
            //Step 0 - File validation
            if (raFileMetaData.getFileName() != null) {
                ErrorDetails validateFileErrorDetails = validateFile(raFileMetaData);
                if (validateFileErrorDetails != null) {
                    upsertIngestionStatus(raFileMetaData, REJECTED, ROSTER_INGESTION_VALIDATION_FAILED, null,
                            validateFileErrorDetails, false);
                    return;
                }
            }

            //Step 1 - Meta data validation
            ErrorDetails validationErrorDetails = ingestionTaskService.validateMetaDataAndGetErrorList(raFileMetaData);
            if (validationErrorDetails != null) {
                log.warn("raFileMetaDataDetails {} has these errors {} in metadata",
                        gson.toJson(raFileMetaData), gson.toJson(validationErrorDetails));
                //TODO fix status code
                upsertIngestionStatus(raFileMetaData, REJECTED, ROSTER_INGESTION_VALIDATION_FAILED,null,
                        validationErrorDetails, false);
                //TODO fix below block
                try {
                    String sourceFilePath = fileSystemUtilService.getSourceFilePath(fileName);
                    String archiveFilePath = fileSystemUtilService.getArchiveFilePath(fileName);
                    fileSystemUtilService.copyFileToDest(sourceFilePath, archiveFilePath);
                } catch (Exception ex) {
                    log.error("Error in copying file on meta data validation failure {}", ex.getMessage());
                }
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
                        standardizedFileName,
                        new ErrorDetails("RI_ERR_MD_4", "System error while copying files"), false);
                return;
            }
            upsertIngestionStatus(raFileMetaData, IN_PROGRESS, ROSTER_INGESTION_COMPLETED,
                    standardizedFileName, null, false);
            if (!deleteFileIfExists(sourceFilePath)) {
                //TODO later do we need to stop process here???
                log.warn("Error deleting sourceFilePath {} - raFileMetaDataDetails {}", sourceFilePath, gson.toJson(raFileMetaData));
            }
            log.debug("IngestionTask done for {}", gson.toJson(getTaskData()));
        } catch (Exception ex) {
            try {
                log.error("Error in IngestionTask raFileMetaData {} ex {} stacktrace {} ", gson.toJson(raFileMetaData),
                        ex.getMessage(), ExceptionUtils.getStackTrace(ex));
                //TODO fix it
                String stacktrace = trimToNChars(ExceptionUtils.getStackTrace(ex), 2000);
                String message = trimToNChars(ex.getMessage(), 1000);
                Long raFileDetailsId = upsertIngestionStatus(raFileMetaData, FileMetaDataTableStatus.valueOf(raFileMetaData.getRAFileProcessingStatus()),
                        ROSTER_INGESTION_FAILED, null, new ErrorDetails("RI_ERR_MD_UNKWN", message),
                        raFileMetaData.getReprocess() != null && raFileMetaData.getReprocess().toUpperCase().startsWith("Y"));

                if (raFileDetailsId != null) {
                    dartRASystemErrorsService.saveDartRASystemErrors(raFileDetailsId, null,
                            "INGESTION", null, "UNKNOWN", ex.getMessage(),
                            stacktrace, 1);
                }
            } catch (Exception ignore) {
                log.error("Error in IngestionTask catch - message {} stacktrace {}", ignore.getMessage(),
                        ExceptionUtils.getStackTrace(ignore));
            }
        } finally {
            log.info("Finally in Ingestion task for {}", gson.toJson(getTaskData()));
            ingestionTaskRunningMap.remove(raFileMetaData.getRaPlmRoFileDataId());
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
            return new ErrorDetails("RI_ERR_MD_3", "File missing");
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

    public Long upsertIngestionStatus(RAFileMetaData raFileMetaData, FileMetaDataTableStatus status, int statusCode, String standardizedFileName, ErrorDetails errorDetails,
                                      boolean reProcess) {
        String fileName = raFileMetaData.getFileName();
        //TODO insert ticket ids
        String market = raFileMetaData.getCntState();
        String lob = raFileMetaData.getLob();
        Long raFileDetailsId = raFileDetailsService.insertRAFileDetails(raFileMetaData.getOrgName(), fileName,
                standardizedFileName, market, statusCode);
        raFileMetaDataDetailsService.insertRAFileDetailsLob(raFileDetailsId, lob, 1);
        if (raFileMetaData.getDcnId() != null) {
            raFileMetaDataDetailsService.insertRARTFileAltIds(raFileDetailsId, raFileMetaData.getDcnId(), AltIdType.DCN_ID.name(), 1);
        }
        if (raFileMetaData.getRoId() != null) {
            raFileMetaDataDetailsService.insertRARTFileAltIds(raFileDetailsId, raFileMetaData.getRoId(), AltIdType.RO_ID.name(), 1);
        }
        String plmRoFileDataId = raFileMetaData.getRaPlmRoFileDataId() == null ? null : String.valueOf(raFileMetaData.getRaPlmRoFileDataId());
        raFileMetaDataDetailsService.insertRARTFileAltIds(raFileDetailsId, plmRoFileDataId, AltIdType.PLM_RO_FILE_DATA_ID.name(),
                1);
        if (raFileMetaData.getPriorityProvYN() != null && raFileMetaData.getPriorityProvYN().equals("Y")) {
            raFileMetaDataDetailsService.insertRARTFileAltIds(raFileDetailsId, "WG",
                    AltIdType.PROVIDER_CATEGORY.name(), 1);
        }
        //TODO need to get contact from file
//        raFileMetaDataDetailsService.insertRARTContactDetails(raFileDetailsId, null,
//                contact, contactType, isActive)
        raFileMetaDataDetailsService.updateRAPlmRoFileDataStatus(raFileMetaData, status, reProcess);
        if (errorDetails != null) {
            //TODO what should be errorCodeDetailsId
            raErrorLogsService.insertRASystemErrors(raFileDetailsId, errorDetails.getErrorCode(),
                    errorDetails.getErrorDescription(), statusCode);
        }
        return raFileDetailsId;
    }
}