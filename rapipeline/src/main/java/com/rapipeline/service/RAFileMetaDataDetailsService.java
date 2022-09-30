package com.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.roster.entity.RAPlmRoFileData;
import com.hilabs.roster.entity.RAPlmRoProfData;
import com.hilabs.roster.entity.RAProvDetails;
import com.rapipeline.dto.ErrorDetails;
import com.rapipeline.dto.RAFileMetaData;
import com.rapipeline.model.ErrorCategory;
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
    public ErrorDetails validateMetaDataAndGetErrorList(RAFileMetaData raFileMetaData) {
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
        if (raFileMetaData.getDcnId() == null) {
            missingFields.add("DCN ID");
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
        if (errorList.size() > 0) {
            return new ErrorDetails(ErrorCategory.INGESTION_MISSING_DATA, gson.toJson(errorList), null);
        }
        return null;
    }
    public void updateRAPlmRoFileDataStatus(RAFileMetaData raFileMetaData, String status) {
        raPlmRoFileDataRepository.updateRAPlmRoFileDataStatus(raFileMetaData.getRaPlmRoFileDataId(), status);
    }
}
