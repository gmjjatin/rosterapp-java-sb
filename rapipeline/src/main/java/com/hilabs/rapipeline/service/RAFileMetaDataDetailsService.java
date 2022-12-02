package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.model.FileMetaDataTableStatus;
import com.hilabs.rapipeline.repository.RAPlmRoFileDataRepository;
import com.hilabs.rapipeline.repository.RAPlmRoProfDataRepository;
import com.hilabs.roster.dto.RAFileMetaData;
import com.hilabs.roster.entity.*;
import com.hilabs.roster.repository.RAFileDetailsLobRepository;
import com.hilabs.roster.repository.RARTContactDetailsRepository;
import com.hilabs.roster.repository.RARTFileAltIdsRepository;
import com.hilabs.roster.repository.RAStatusCDMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.hilabs.rapipeline.model.FileMetaDataTableStatus.*;
import static com.hilabs.roster.dto.AltIdType.PLM_RO_FILE_DATA_ID;

@Service
@Slf4j
public class RAFileMetaDataDetailsService {
    private static final Gson gson = new Gson();
    @Autowired
    private RAPlmRoFileDataRepository raPlmRoFileDataRepository;

    @Autowired
    private RAPlmRoProfDataRepository raPlmRoProfDataRepository;

    @Autowired
    private RAFileDetailsLobRepository raFileDetailsLobRepository;

    @Autowired
    private RARTContactDetailsRepository raRTContactDetailsRepository;

    @Autowired
    private RARTFileAltIdsRepository rartFileAltIdsRepository;

    @Autowired
    private RAStatusCDMasterRepository raStatusCDMasterRepository;

//    public Long insertRAProvMarketLobMap(Long raProvDetailsId, String market, String lob, Integer isActive) {
//        RAProvMarketLobMap raProvMarketLobMap = new RAProvMarketLobMap(raProvDetailsId, market, lob, isActive);
//        raProvMarketLobMap = raProvMarketLobMapRepository.save(raProvMarketLobMap);
//        return raProvMarketLobMap.getId();
//    }

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

    @Transactional
    public List<RAFileMetaData> getNewFileMetaDataDetailsAndUpdateToInQueue(int count) {
        List<RAPlmRoFileData> raPlmRoFileDataList = raPlmRoFileDataRepository.getNewRAPlmRoFileDataListWithStatus(NEW.name(), count);
        raPlmRoFileDataRepository.updateRAPlmRoFileDataListWithStatus(IN_QUEUE.name(),
                raPlmRoFileDataList.stream().map(RAPlmRoFileData::getRaPlmRoFileDataId).collect(Collectors.toList()));
        Set<Long> set = new HashSet<>();
        List<RAFileMetaData> raFileMetaDataList = new ArrayList<>();
        for (RAPlmRoFileData raPlmRoFileData : raPlmRoFileDataList) {
            if (set.contains(raPlmRoFileData.getRaPlmRoFileDataId())) {
                continue;
            }
            set.add(raPlmRoFileData.getRaPlmRoFileDataId());
            Optional<RAPlmRoProfData> optionalRAPlmRoProfData = raPlmRoProfDataRepository.findById(raPlmRoFileData.getRaPlmRoProfDataId());
            if (!optionalRAPlmRoProfData.isPresent()) {
                log.error("RAPlmRoProfData missing for raPlmRoFileData {}", gson.toJson(raPlmRoFileData));
                continue;
            }
            raFileMetaDataList.add(new RAFileMetaData(optionalRAPlmRoProfData.get(), raPlmRoFileData));
        }
        return raFileMetaDataList;
    }

//    public List<RAFileMetaData> getNewAndReProcessFileMetaDataDetails(int count) {
//        List<RAPlmRoFileData> raPlmRoFileDataList = new ArrayList<>(raPlmRoFileDataRepository.getNewRAPlmRoFileDataListWithStatus(NEW.name(), count));
//        raPlmRoFileDataList.addAll(raPlmRoFileDataRepository.getReprocessRAPlmRoFileDataListWithStatus(PENDING.name()));
//        List<RAFileMetaData> raFileMetaDataList = new ArrayList<>();
//        Set<Long> set = new HashSet<>();
//        for (RAPlmRoFileData raPlmRoFileData : raPlmRoFileDataList) {
//            if (set.contains(raPlmRoFileData.getRaPlmRoFileDataId())) {
//                continue;
//            }
//            set.add(raPlmRoFileData.getRaPlmRoFileDataId());
//            Optional<RAPlmRoProfData> optionalRAPlmRoProfData = raPlmRoProfDataRepository.findById(raPlmRoFileData.getRaPlmRoProfDataId());
//            if (!optionalRAPlmRoProfData.isPresent()) {
//                log.error("RAPlmRoProfData missing for raPlmRoFileData {}", gson.toJson(raPlmRoFileData));
//                continue;
//            }
//            raFileMetaDataList.add(new RAFileMetaData(optionalRAPlmRoProfData.get(), raPlmRoFileData));
//        }
//        return raFileMetaDataList;
//    }

    public Optional<RAPlmRoFileData> findById(Long raPlmRoFileDataId) {
        return raPlmRoFileDataRepository.findById(raPlmRoFileDataId);
    }
    public void updateRAPlmRoFileDataStatus(RAFileMetaData raFileMetaData, FileMetaDataTableStatus status, boolean reProcess) {
        raPlmRoFileDataRepository.updateRAPlmRoFileDataStatus(raFileMetaData.getRaPlmRoFileDataId(), status != null ? status.name() : null,
                reProcess ? "Y" : "N");
    }

    public boolean isReprocess(RAFileMetaData raFileMetaData) {
        if (raFileMetaData == null || raFileMetaData.getReprocess() == null) {
            return false;
        }
        String reProcess = raFileMetaData.getReprocess();
        if (!reProcess.toUpperCase().startsWith("Y")) {
            return false;
        }
        return raFileMetaData.getRAFileProcessingStatus() != null
                && raFileMetaData.getRAFileProcessingStatus().equalsIgnoreCase(PENDING.name());

    }

    //TODO move to right file

    public void updatePlmStatusForFileDetailsId(Long raFileDetailsId, FileMetaDataTableStatus fileMetaDataTableStatus) {
        log.info("updatePlmStatusForFileDetailsId for raFileDetailsId {} fileMetaDataTableStatus {}", raFileDetailsId, fileMetaDataTableStatus);
        if (fileMetaDataTableStatus == null) {
            log.error("Can't update null status for raFileDetailsId {}", raFileDetailsId);
            return;
        }
        List<RARTFileAltIds> rartFileAltIdsList = rartFileAltIdsRepository.findByRAFileDetailsIdList(Collections.singletonList(raFileDetailsId),
                PLM_RO_FILE_DATA_ID.name());
        Optional<RARTFileAltIds> optionalRARTFileAltIds = rartFileAltIdsList.stream().filter(p -> p.getAltId() != null).findFirst();
        if (!optionalRARTFileAltIds.isPresent()) {
            log.error("Can't find PLM_RO_FILE_DATA_ID for raFileDetailsId {}", raFileDetailsId);
            return;
        }
        RARTFileAltIds rartFileAltIds = optionalRARTFileAltIds.get();
        Long raPlmRoFileDataId = getLongFromStr(rartFileAltIds.getAltId());
        if (raPlmRoFileDataId == null) {
            log.error("Invalid raFileDetailsId {} altId {}", raFileDetailsId, rartFileAltIds.getAltId());
            return;
        }
        raPlmRoFileDataRepository.updateRAPlmRoFileDataListWithStatus(fileMetaDataTableStatus.name(), Collections.singletonList(raPlmRoFileDataId));
    }

    public static Long getLongFromStr(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ex) {
            return null;
        }
    }

}
