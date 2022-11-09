package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.dto.ErrorDetails;
import com.hilabs.rapipeline.dto.RAFileMetaData;
import com.hilabs.roster.entity.RAPlmRoFileData;
import com.hilabs.roster.entity.RARTMarketLobVald;
import com.hilabs.roster.repository.RARTMarketLobValdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.hilabs.rapipeline.model.FileMetaDataTableStatus.NEW;

@Service
@Slf4j
public class IngestionTaskService {
    private static Gson gson = new Gson();

    @Autowired
    private RARTMarketLobValdRepository rartMarketLobValdRepository;
    @Autowired
    private RAProvMarketLobMapService raProvMarketLobMapService;

    public static ConcurrentHashMap<Long, String> ingestionTaskRunningMap = new ConcurrentHashMap<>();

    @Autowired
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

//    public boolean shouldRun(RAFileMetaData raFileMetaData) {
//        //TODO confirm
//        Long plmRoFileDataId = raFileMetaData.getRaPlmRoFileDataId();
//        if (ingestionTaskRunningMap.containsKey(plmRoFileDataId)) {
//            log.warn("Ingestion task in progress for raFileMetaData {}", gson.toJson(raFileMetaData));
//        }
//        return isShouldReprocess(raFileMetaData);
//    }

//    public boolean isShouldReprocess(RAFileMetaData raFileMetaData) {
//        Optional<RAPlmRoFileData> raPlmRoFileDataOptional = raFileMetaDataDetailsService
//                .findById(raFileMetaData.getRaPlmRoFileDataId());
//        if (raPlmRoFileDataOptional.isPresent()) {
//            RAPlmRoFileData raPlmRoFileData = raPlmRoFileDataOptional.get();
//            if (raPlmRoFileData.getRaFileProcessingStatus() != null && raPlmRoFileData.getRaFileProcessingStatus().equalsIgnoreCase(NEW.name())) {
//                return true;
//            }
//            return raPlmRoFileData.getReProcess() != null && raPlmRoFileData.getReProcess().toUpperCase().startsWith("Y");
//        }
//        return false;
//    }

    //TODO later - need to add more checks
    public ErrorDetails validateMetaDataAndGetErrorList(RAFileMetaData raFileMetaData) {
        List<String> missingFields = new ArrayList<>();
        String errorCode = "RI_ERR_MD_1";
        if (raFileMetaData.getOrgName() == null) {
            missingFields.add("Organization Name");
        }
        //TODO fix it. Is it plm ticket id??
        if (raFileMetaData.getRoId() == null) {
            missingFields.add("RO ID");
        }
        if (raFileMetaData.getDcnId() == null) {
            missingFields.add("DCN ID");
        }
        if (raFileMetaData.getFileName() == null) {
            missingFields.add("File Name");
        }
        if (raFileMetaData.getCntState() == null) {
            missingFields.add("Cnt State");
        }
        if (raFileMetaData.getLob() == null) {
            missingFields.add("PLM Network");
        }
        List<String> errorList = new ArrayList<>();
        if (missingFields.size() > 0) {
            errorList.add("Missing fields - " + String.join(", ", missingFields));
        }
        if (!raFileMetaData.getFileName().endsWith(".xlsx")) {
            errorList.add("File name doesn't end with .xlsx");
        }
        if (raFileMetaData.getLob() != null && raFileMetaData.getCntState() != null) {
            String market = raFileMetaData.getCntState();
            String lob = raFileMetaData.getLob();
            List<RARTMarketLobVald> marketLobValds = rartMarketLobValdRepository.getByMarket(market);
            if (!marketLobValds.stream().anyMatch(p -> p.getLob() != null && p.getLob().equals(lob))) {
                errorCode = "RI_ERR_MD_2";
                errorList.add("Invalid Cnt State and PLM Network combination");
            }
        }
        if (errorList.size() > 0) {
            return new ErrorDetails(errorCode, String.join(", ", errorList));
        }
        return null;
    }

    private static void downloadUsingNIO(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
