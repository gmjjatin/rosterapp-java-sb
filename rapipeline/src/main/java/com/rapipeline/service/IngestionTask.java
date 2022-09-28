package com.rapipeline.service;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.rapipeline.entity.RAFileMetaDataDetails;
import com.rapipeline.entity.RAProvDetails;
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
public class IngestionTask extends Task {
    //TODO later fix it
    public static Long PROCESS_USER_ID = 1L;

    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;
    private FileSystemUtilService fileSystemUtilService;
    private RAFileDetailsService raFileDetailsService;
    private RAProviderService raProviderService;

    private static final Gson gson = new Gson();

    public IngestionTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.fileSystemUtilService = (FileSystemUtilService) applicationContext.getBean("fileSystemUtilService");
        this.raFileMetaDataDetailsService = (RAFileMetaDataDetailsService) applicationContext.getBean("RAFileMetaDataDetailsService");
        this.raFileDetailsService = (RAFileDetailsService) applicationContext.getBean("RAFileDetailsService");
        this.raProviderService = (RAProviderService) applicationContext.getBean("RAProviderService");
    }

    @Override
    public void run() {
        log.info("IngestionTask stared for {}", gson.toJson(getTaskData()));
        Map<String, Object> taskData = getTaskData();
        if (!taskData.containsKey("data")) {
            log.warn("taskData doesn't have key data - taskData {}", gson.toJson(taskData));
            return;
        }
        if (!(taskData.get("data") instanceof RAFileMetaDataDetails)) {
            log.warn("data field in taskData doesn't have valid data - data {} taskData {}", gson.toJson(taskData.get("data")),
                    gson.toJson(taskData));
            return;
        }
        //Meta data validation
        RAFileMetaDataDetails raFileMetaDataDetails = (RAFileMetaDataDetails) taskData.get("data");
        List<String> validationErrorList = new ArrayList<>(raFileMetaDataDetailsService.validateMetaDataAndGetErrorList(raFileMetaDataDetails));
        Optional<RAProvDetails> optionalRAProvDetails = raProviderService.findByProvider(raFileMetaDataDetails.getProviderName());
        if (!optionalRAProvDetails.isPresent()) {
            log.warn("Unknown provider {} taskData {}", raFileMetaDataDetails.getProviderName(), gson.toJson(taskData));
            validationErrorList.add("Unknown provider");
        }
        if (validationErrorList.size() > 0) {
            log.warn("raFileMetaDataDetails {} has these errors {} in metadata", gson.toJson(raFileMetaDataDetails), gson.toJson(validationErrorList));
            raFileMetaDataDetailsService.incrementRetryNoForRAFileMetaDataDetails(raFileMetaDataDetails);
            return;
        }
        RAProvDetails raProvDetails = optionalRAProvDetails.get();
        //File validation
        if (!validateFile(raFileMetaDataDetails)) {
            raFileMetaDataDetailsService.incrementRetryNoForRAFileMetaDataDetails(raFileMetaDataDetails);
            return;
        }
        String plmTicketId = raFileMetaDataDetails.getPlmTicketId();
        String fileName = raFileMetaDataDetails.getFileName();
        String standardizedFileName = getStandardizedFileName(raFileMetaDataDetails);
        String sourceFilePath = fileSystemUtilService.getSourceFilePath(fileName);
        String destinationFilePath = fileSystemUtilService.getDestinationFilePath(standardizedFileName);
        String archiveFilePath = fileSystemUtilService.getArchiveFilePath(fileName);
        String password = raFileMetaDataDetails.getPassword();
        //Copy file to destination
        if (!copyToDestAndArchive(sourceFilePath, destinationFilePath, archiveFilePath, password)) {
            log.warn("Copying files failed for raFileMetaDataDetails {}", gson.toJson(raFileMetaDataDetails));
            return;
        }
        raFileDetailsService.insertRAFileDetails(raProvDetails.getId(), fileName, standardizedFileName, plmTicketId, destinationFilePath, null, PROCESS_USER_ID, PROCESS_USER_ID);
        //Updating final status
        if (!raFileMetaDataDetailsService.updateStatusForRAFileMetaDataDetails(raFileMetaDataDetails, 1)) {
            log.warn("Error updating ingestion status - raFileMetaDataDetails {}", gson.toJson(raFileMetaDataDetails));
            return;
        }
        if (!deleteFileIfExists(sourceFilePath)) {
            //TODO later do we need to stop process here???
            log.warn("Error deleting sourceFilePath {} - raFileMetaDataDetails {}", sourceFilePath, gson.toJson(raFileMetaDataDetails));
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

    public String getStandardizedFileName(RAFileMetaDataDetails raFileMetaDataDetails) {
        String plmTicketId = raFileMetaDataDetails.getPlmTicketId();
        Date createdDate = raFileMetaDataDetails.getCreatedDate();
        SimpleDateFormat formatter = new SimpleDateFormat("YYYYMMDDHHmmss");
        String strDate = formatter.format(createdDate);
        return plmTicketId + "-" + strDate + ".xlsx";
    }

    public boolean validateFile(RAFileMetaDataDetails raFileMetaDataDetails) {
        String sourceFilePath = fileSystemUtilService.getSourceFilePath(raFileMetaDataDetails.getFileName());
        File file = new File(sourceFilePath);
        if (!file.exists()) {
            log.warn("File with name {} doesn't exists - raFileMetaDataDetails {}", raFileMetaDataDetails.getFileName(), gson.toJson(raFileMetaDataDetails));
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
}
