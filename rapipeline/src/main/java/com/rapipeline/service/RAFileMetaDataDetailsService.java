package com.rapipeline.service;

import com.google.gson.Gson;
import com.rapipeline.dto.RAFileMetaData;
import com.rapipeline.entity.RAPlmRoFileData;
import com.rapipeline.entity.RAPlmRoProfData;
import com.rapipeline.entity.RAProvDetails;
import com.rapipeline.repository.RAPlmRoFileDataRepository;
import com.rapipeline.repository.RAPlmRoProfDataRepository;
import com.rapipeline.repository.RAProvDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RAFileMetaDataDetailsService {
    private static final Gson gson = new Gson();
    @Autowired
    private RAPlmRoFileDataRepository raPlmRoFileDataRepository;

    @Autowired
    private RAPlmRoProfDataRepository raPlmRoProfDataRepository;

    @Autowired
    private RAProvDetailsRepository raProvDetailsRepository;
    public List<RAFileMetaData> getUnIngestedRAFileMetaDataDetails() {
        List<RAPlmRoFileData> raPlmRoFileDataList = raPlmRoFileDataRepository.getNewRAPlmRoFileDataList();
        List<RAFileMetaData> raFileMetaDataList = new ArrayList<>();
        for (RAPlmRoFileData raPlmRoFileData : raPlmRoFileDataList) {
            Optional<RAPlmRoProfData> optionalRAPlmRoProfData = raPlmRoProfDataRepository.findById(raPlmRoFileData.getRaPlmRoProfDataId());
            if (!optionalRAPlmRoProfData.isPresent()) {
                log.error("RAPlmRoProfData missing for raPlmRoFileData {}", gson.toJson(raPlmRoFileData));
                continue;
            }
            raFileMetaDataList.add(new RAFileMetaData(optionalRAPlmRoProfData.get(), raPlmRoFileData));
        }
        return raFileMetaDataList;
    }

    //TODO later - need to add more checks
    public List<String> validateMetaDataAndGetErrorList(RAFileMetaData raFileMetaData) {
        List<String> missingFields = new ArrayList<>();
        if (raFileMetaData.getOrgName() == null) {
            missingFields.add("Organization Name");
        }
        if (raFileMetaData.getCntState() == null) {
            missingFields.add("Cnt State");
        }
        if (raFileMetaData.getPlmNetwork() == null) {
            missingFields.add("PLM Network");
        }
        //TODO fix it. Is it plm ticket id??
        if (raFileMetaData.getRoId() == null) {
            missingFields.add("RO ID");
        }
        if (raFileMetaData.getFileName() == null) {
            missingFields.add("File Name");
        }
        List<String> errorList = new ArrayList<>();
        if (missingFields.size() > 0) {
            errorList.add("Missing fields - " + String.join(", ", missingFields));
        }
        if (!raFileMetaData.getFileName().endsWith(".xlsx")) {
            errorList.add("File name doesn't end with .xlsx");
        }
        Optional<RAProvDetails> optionalRAProvDetails = raProvDetailsRepository.findByProvider(raFileMetaData.getOrgName());
        if (!optionalRAProvDetails.isPresent()) {
            errorList.add("Unknown provider");
        }
        return errorList;
    }

//    public boolean updateStatusForRAFileMetaDataDetails(RAFileMetaDataDetails raFileMetaDataDetails, int ingestionStatus) {
//        try {
//            raFileMetaDataDetailsRepository.updateStatusForRAFileMetaDataDetails(raFileMetaDataDetails.getId(), ingestionStatus);
//            return true;
//        } catch (Exception ex) {
//            log.error("Error in updateRAFileMetaDataDetails - raFileMetaDataDetails {} ingestionStatus {} ex {}", gson.toJson(raFileMetaDataDetails),
//                    ingestionStatus, ex.getMessage());
//            return false;
//        }
//    }
//
    public void updateRAPlmRoFileDataStatus(RAFileMetaData raFileMetaData, String status) {
        raPlmRoFileDataRepository.updateRAPlmRoFileDataStatus(raFileMetaData.getRaPlmRoFileDataId(), status);
    }
}
