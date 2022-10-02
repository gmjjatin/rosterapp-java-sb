package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.dto.ErrorDetails;
import com.hilabs.rapipeline.dto.RAFileMetaData;
import com.hilabs.rapipeline.model.ErrorCategory;
import com.hilabs.rapipeline.repository.RAPlmRoFileDataRepository;
import com.hilabs.rapipeline.repository.RAPlmRoProfDataRepository;
import com.hilabs.roster.entity.RAPlmRoFileData;
import com.hilabs.roster.entity.RAPlmRoProfData;
import com.hilabs.roster.entity.RAProvDetails;
import com.hilabs.roster.entity.RAProvMarketLobMap;
import com.hilabs.roster.repository.RAProvDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IngestionValidationService {
    private static final Gson gson = new Gson();
    @Autowired
    private RAProvDetailsRepository raProvDetailsRepository;
    @Autowired
    private RAProvMarketLobMapService raProvMarketLobMapService;

    //TODO later - need to add more checks
    public ErrorDetails validateMetaDataAndGetErrorList(RAFileMetaData raFileMetaData) {
        List<String> missingFields = new ArrayList<>();
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
        List<String> errorList = new ArrayList<>();
        if (missingFields.size() > 0) {
            errorList.add("Missing fields - " + String.join(", ", missingFields));
        }
        if (!raFileMetaData.getFileName().endsWith(".xlsx")) {
            errorList.add("File name doesn't end with .xlsx");
        }
        if (raFileMetaData.getCntState() == null) {
            missingFields.add("Cnt State");
        }
        if (raFileMetaData.getPlmNetwork() == null) {
            missingFields.add("PLM Network");
        }
        Optional<RAProvDetails> optionalRAProvDetails = raProvDetailsRepository.findByProvider(raFileMetaData.getOrgName());
        if (!optionalRAProvDetails.isPresent()) {
            errorList.add("Provider not listed");
        }
        if (optionalRAProvDetails.isPresent() && raFileMetaData.getPlmNetwork() != null && raFileMetaData.getCntState() != null) {
            RAProvDetails raProvDetails = optionalRAProvDetails.get();
            String lob = raFileMetaData.getPlmNetwork();
            String market = raFileMetaData.getCntState();
            List<RAProvMarketLobMap> raProvMarketLobMapList = raProvMarketLobMapService
                    .getRAProvMarketLobMapForProvider(raProvDetails.getId());
            //TODO confirm if comma separated
            boolean isKnownNetwork = raProvMarketLobMapList.stream().anyMatch(p -> {
                String m = p.getMarket();
                String pLob = p.getLob();
                if (m == null || pLob == null) {
                    return false;
                }
                return m.equalsIgnoreCase(market) && pLob.equalsIgnoreCase(lob);
            });
            if (!isKnownNetwork) {
                errorList.add(String.format("PlmNetwork %s CntState %s Organization Name %s is not supported", lob, market, raFileMetaData.getOrgName()));
            }
        }
        if (errorList.size() > 0) {
            return new ErrorDetails("Meta data validation failed", gson.toJson(errorList));
        }
        return null;
    }
}
