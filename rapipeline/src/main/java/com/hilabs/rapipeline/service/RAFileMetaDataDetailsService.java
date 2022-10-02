package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.dto.RAFileMetaData;
import com.hilabs.rapipeline.repository.RAPlmRoFileDataRepository;
import com.hilabs.rapipeline.repository.RAPlmRoProfDataRepository;
import com.hilabs.roster.entity.*;
import com.hilabs.roster.repository.*;
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

    @Autowired
    private RAProvMarketLobMapRepository raProvMarketLobMapRepository;

    @Autowired
    private RAFileDetailsLobRepository raFileDetailsLobRepository;

    @Autowired
    private RARTContactDetailsRepository raRTContactDetailsRepository;

    @Autowired
    private RARTFileAltIdsRepository rartFileAltIdsRepository;

    public Long insertRAProvMarketLobMap(Long raProvDetailsId, String market, String lob, Integer isActive) {
        RAProvMarketLobMap raProvMarketLobMap = new RAProvMarketLobMap(raProvDetailsId, market, lob, isActive);
        raProvMarketLobMap = raProvMarketLobMapRepository.save(raProvMarketLobMap);
        return raProvMarketLobMap.getId();
    }

    public Long insertRAFileDetailsLob(Long raFileDetailsId, String lob, Integer isActive) {
        RAFileDetailsLob raFileDetailsLob = new RAFileDetailsLob(raFileDetailsId, lob, isActive);
        raFileDetailsLob = raFileDetailsLobRepository.save(raFileDetailsLob);
        return raFileDetailsLob.getId();
    }

    public Long insertRARTContactDetails(Long raFileDetailsId, Long raSheetDetailsId, String contact, String contactType, Integer isActive) {
        RARTContactDetails rartContactDetails = new RARTContactDetails(raFileDetailsId, raSheetDetailsId,
                contact, contactType, isActive);
        rartContactDetails = raRTContactDetailsRepository.save(rartContactDetails);
        return rartContactDetails.getId();
    }

    public Long insertRARTFileAltIds(Long raFileDetailsId, String altId, String altIdType, Integer isActive) {
        RARTFileAltIds rartFileAltIds = new RARTFileAltIds(raFileDetailsId, altId, altIdType, isActive);
        rartFileAltIds = rartFileAltIdsRepository.save(rartFileAltIds);
        return rartFileAltIds.getId();
    }
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

    public Optional<RAPlmRoFileData> findById(Long raPlmRoFileDataId) {
        return raPlmRoFileDataRepository.findById(raPlmRoFileDataId);
    }
    public void updateRAPlmRoFileDataStatus(RAFileMetaData raFileMetaData, String status) {
        raPlmRoFileDataRepository.updateRAPlmRoFileDataStatus(raFileMetaData.getRaPlmRoFileDataId(), status);
    }
}
