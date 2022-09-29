package com.rapipeline.ingestion;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.rapipeline.dto.ErrorDetails;
import com.rapipeline.dto.RAFileMetaData;
import com.rapipeline.entity.RAFileDetails;
import com.rapipeline.entity.RAProvDetails;
import com.rapipeline.model.ErrorCategory;
import com.rapipeline.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.rapipeline.util.PipelineStatusCodeUtil.*;

@Slf4j
//TODO what if same file is placed again???
//TODO handle Reject files not in 6 states/GBD markets/white glove providers??
public class IngestionTask extends Task {
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;
    private FileSystemUtilService fileSystemUtilService;
    private RAFileDetailsService raFileDetailsService;
    private RAProviderService raProviderService;

    private RAFileXStatusService raFileXStatusService;
    private RASystemErrorsService raSystemErrorsService;

    private static final Gson gson = new Gson();

    public IngestionTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.fileSystemUtilService = (FileSystemUtilService) applicationContext.getBean("fileSystemUtilService");
        this.raFileMetaDataDetailsService = (RAFileMetaDataDetailsService) applicationContext.getBean("RAFileMetaDataDetailsService");
        this.raFileDetailsService = (RAFileDetailsService) applicationContext.getBean("RAFileDetailsService");
        this.raProviderService = (RAProviderService) applicationContext.getBean("RAProviderService");
        this.raFileXStatusService = (RAFileXStatusService) applicationContext.getBean("RAFileXStatusService");
        this.raSystemErrorsService = (RASystemErrorsService) applicationContext.getBean("RASystemErrorsService");
    }

    @Override
    public void run() {
        log.info("IngestionTask stared for {}", gson.toJson(getTaskData()));
        Map<String, Object> taskData = getTaskData();
        //Core framework level errors should change anything related to file
        if (!taskData.containsKey("data")) {
            log.warn("taskData doesn't have key data - taskData {}", gson.toJson(taskData));
            return;
        }
        if (!(taskData.get("data") instanceof RAFileMetaData)) {
            log.warn("data field in taskData doesn't have valid data - data {} taskData {}", gson.toJson(taskData.get("data")),
                    gson.toJson(taskData));
            return;
        }
        RAFileMetaData raFileMetaData = (RAFileMetaData) taskData.get("data");

        //Meta data validation
        List<String> validationErrorList = raFileMetaDataDetailsService.validateMetaDataAndGetErrorList(raFileMetaData);
        if (validationErrorList.size() > 0) {
            log.warn("raFileMetaDataDetails {} has these errors {} in metadata", gson.toJson(raFileMetaData), gson.toJson(validationErrorList));
            Optional<RAProvDetails> optionalRAProvDetails = raProviderService.findByProvider(raFileMetaData.getOrgName());
            upsertIngestionStatus(raFileMetaData, REJECTED_STATUS, REJECTED_STATUS_CODE,
                    optionalRAProvDetails.<Long>map(RAProvDetails::getId).orElse(null), null, null
            , new ErrorDetails(ErrorCategory.INGESTION_MISSING_DATA, gson.toJson(validationErrorList), null));
            return;
        }

        //Already checked in validateMetaDataAndGetErrorList
        Optional<RAProvDetails> optionalRAProvDetails = raProviderService.findByProvider(raFileMetaData.getOrgName());
        RAProvDetails raProvDetails = optionalRAProvDetails.get();
        //File validation
        if (!validateFile(raFileMetaData)) {
            upsertIngestionStatus(raFileMetaData, REJECTED_STATUS, REJECTED_STATUS_CODE,
                    optionalRAProvDetails.<Long>map(RAProvDetails::getId).orElse(null), null, null,
                    new ErrorDetails(ErrorCategory.INGESTION_FILE_MISSING, "File missing from source folder", null));
            return;
        }
        //TODO fix plm ticket id
        String plmTicketId = raFileMetaData.getRoId();
        String fileName = raFileMetaData.getFileName();
        String standardizedFileName = getStandardizedFileName(raFileMetaData);
        String sourceFilePath = fileSystemUtilService.getSourceFilePath(fileName);
        String destinationFilePath = fileSystemUtilService.getDestinationFilePath(standardizedFileName);
        String archiveFilePath = fileSystemUtilService.getArchiveFilePath(fileName);
        //TODO fix password
        String password = "123456";
//        String password = raFileMetaData.getPassword();
        //Copy file to destination
        if (!copyToDestAndArchive(sourceFilePath, destinationFilePath, archiveFilePath, password)) {
            log.warn("Copying files failed for raFileMetaDataDetails {}", gson.toJson(raFileMetaData));
            upsertIngestionStatus(raFileMetaData, REJECTED_STATUS, REJECTED_STATUS_CODE,
                    optionalRAProvDetails.<Long>map(RAProvDetails::getId).orElse(null),
                    standardizedFileName, destinationFilePath,
                    new ErrorDetails());
            return;
        }
        boolean updated = upsertIngestionStatus(raFileMetaData, IN_PROGRESS_STATUS, INGESTED_STATUS_CODE, raProvDetails.getId(),
                standardizedFileName, destinationFilePath);
        if (!updated) {
            log.warn("upsertIngestionStatus is not successful - raFileMetaDataDetails {}", gson.toJson(raFileMetaData));
            return;
        }
        if (!deleteFileIfExists(sourceFilePath)) {
            //TODO later do we need to stop process here???
            log.warn("Error deleting sourceFilePath {} - raFileMetaDataDetails {}", sourceFilePath, gson.toJson(raFileMetaData));
        }
        log.debug("IngestionTask done for {}", gson.toJson(getTaskData()));
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

    public boolean validateFile(RAFileMetaData raFileMetaData) {
        String sourceFilePath = fileSystemUtilService.getSourceFilePath(raFileMetaData.getFileName());
        File file = new File(sourceFilePath);
        if (!file.exists()) {
            log.warn("File with name {} doesn't exists - raFileMetaDataDetails {}", raFileMetaData.getFileName(), gson.toJson(raFileMetaData));
            return false;
        }
        return true;
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

    public boolean upsertIngestionStatus(RAFileMetaData raFileMetaData, String status, int statusCode,
                                         Long raProvDetailsId, String standardizedFileName,
                                         String destinationFilePath, ErrorDetails errorDetails) {
        try {
            String fileName = raFileMetaData.getFileName();
            String plmTicketId = raFileMetaData.getRoId();
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findByFileName(fileName);
            if (!optionalRAFileDetails.isPresent()) {
                raFileDetailsService.insertRAFileDetails(raProvDetailsId, raFileMetaData.getCntState(), raFileMetaData.getPlmNetwork(),fileName, standardizedFileName,
                        plmTicketId, destinationFilePath, null, PROCESS_USER_ID, PROCESS_USER_ID);
                optionalRAFileDetails = raFileDetailsService.findByFileName(fileName);
            } else {
                raFileDetailsService.updateRAFileDetails(optionalRAFileDetails.get(), raProvDetailsId, raFileMetaData.getCntState(), raFileMetaData.getPlmNetwork(),fileName, standardizedFileName,
                        plmTicketId, destinationFilePath, null, PROCESS_USER_ID);
            }
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            raFileMetaDataDetailsService.updateRAPlmRoFileDataStatus(raFileMetaData, status);
            raFileXStatusService.insertOrUpdateRAFileXStatus(raFileDetails.getId(), statusCode);
            raSystemErrorsService.insertRASystemErrors(raFileDetails.getId(), "INGESTION",
                    statusCode, errorDetails.getErrorCategory(), errorDetails.getErrorDescription(), errorDetails.getErrorStackTrace(),
                    PROCESS_USER_ID, PROCESS_USER_ID);
            return true;
        } catch (Exception ex) {
            log.error("Error in upsertIngestionStatus - raFileMetaData {} ex {}", gson.toJson(raFileMetaData), ex.getMessage());
            return false;
        }
    }
}
