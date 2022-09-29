package com.rapipeline.ingestion;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.rapipeline.dto.RAFileMetaData;
import com.rapipeline.entity.RAFileDetails;
import com.rapipeline.entity.RAProvDetails;
import com.rapipeline.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
//TODO what if same file is placed again???
//TODO handle Reject files not in 6 states/GBD markets/white glove providers??
//TODO DCN id in standarized name
//TODO handle market and lob in ra_file_details
public class IngestionTask extends Task {
    //TODO later fix it
    public static Long PROCESS_USER_ID = 1L;
    public static String REJECTED_STATUS = "REJECTED";

    //TODO
    public static int REJECTED_STATUS_CODE = 2;
    public static int INGESTED_STATUS_CODE = 1;
    public static String IN_PROGRESS_STATUS = "IN-PROGRESS";

    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;
    private FileSystemUtilService fileSystemUtilService;
    private RAFileDetailsService raFileDetailsService;
    private RAProviderService raProviderService;

    private RAFileXStatusService raFileXStatusService;

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
    }

    @Override
    public void run() {
        log.info("IngestionTask stared for {}", gson.toJson(getTaskData()));
        Map<String, Object> taskData = getTaskData();
        if (!taskData.containsKey("data")) {
            log.warn("taskData doesn't have key data - taskData {}", gson.toJson(taskData));
            return;
        }
        if (!(taskData.get("data") instanceof RAFileMetaData)) {
            log.warn("data field in taskData doesn't have valid data - data {} taskData {}", gson.toJson(taskData.get("data")),
                    gson.toJson(taskData));
            return;
        }

        //Meta data validation
        RAFileMetaData raFileMetaData = (RAFileMetaData) taskData.get("data");
        Optional<RAProvDetails> optionalRAProvDetails = raProviderService.findByProvider(raFileMetaData.getOrgName());
        List<String> validationErrorList = raFileMetaDataDetailsService.validateMetaDataAndGetErrorList(raFileMetaData);
        if (validationErrorList.size() > 0) {
            log.warn("raFileMetaDataDetails {} has these errors {} in metadata", gson.toJson(raFileMetaData), gson.toJson(validationErrorList));
            upsertIngestionStatus(raFileMetaData, REJECTED_STATUS, REJECTED_STATUS_CODE,
                    optionalRAProvDetails.<Long>map(RAProvDetails::getId).orElse(null), null, null);
            return;
        }

        //Already checked in validateMetaDataAndGetErrorList
        RAProvDetails raProvDetails = optionalRAProvDetails.get();
        //File validation
        if (!validateFile(raFileMetaData)) {
            raFileMetaDataDetailsService.updateRAPlmRoFileDataStatus(raFileMetaData, REJECTED_STATUS);
            upsertIngestionStatus(raFileMetaData, REJECTED_STATUS, REJECTED_STATUS_CODE,
                    optionalRAProvDetails.<Long>map(RAProvDetails::getId).orElse(null), null, null);
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
                    standardizedFileName, destinationFilePath);
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
                                         String destinationFilePath) {
        try {
            String fileName = raFileMetaData.getFileName();
            String plmTicketId = raFileMetaData.getRoId();
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findByFileName(fileName);
            if (!optionalRAFileDetails.isPresent()) {
                raFileDetailsService.insertRAFileDetails(raProvDetailsId, fileName, standardizedFileName,
                        plmTicketId, destinationFilePath, null, PROCESS_USER_ID, PROCESS_USER_ID);
                optionalRAFileDetails = raFileDetailsService.findByFileName(fileName);
            } else {
                //TODO update the file details
            }
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            raFileDetailsService.insertRAFileDetails(raProvDetailsId, fileName, standardizedFileName,
                    plmTicketId, destinationFilePath, null, PROCESS_USER_ID, PROCESS_USER_ID);
            raFileMetaDataDetailsService.updateRAPlmRoFileDataStatus(raFileMetaData, status);
            raFileXStatusService.insertOrUpdateRAFileXStatus(raFileDetails.getId(), statusCode);
            return true;
        } catch (Exception ex) {
            log.error("Error in upsertIngestionStatus - raFileMetaData {} ex {}", gson.toJson(raFileMetaData), ex.getMessage());
            return false;
        }
    }
}
