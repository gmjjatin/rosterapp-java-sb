package com.anthem.rostertracker.service;

import com.anthem.rostertracker.entity.RAConvStatusStageMappingInfo;
import com.anthem.rostertracker.model.*;
import com.anthem.rostertracker.repository.RosterConvStatusStageMappingInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RosterConvStatusStageMappingInfoService {
    @Autowired
    private RosterConvStatusStageMappingInfoRepository rosterConvStatusStageMappingInfoRepository;

    public static List<RAConvStatusStageMappingInfo> allRAConvStatusStageMappingInfo = null;
    public List<RAConvStatusStageMappingInfo> getAllRosterConvMappingInfoList() {
        if (allRAConvStatusStageMappingInfo == null) {
            allRAConvStatusStageMappingInfo = rosterConvStatusStageMappingInfoRepository.findAll();
        }
        return allRAConvStatusStageMappingInfo;
    }

    public RosterFileProcessStage getRosterFileProcessStage(RosterFileProcessStatus rosterFileProcessStatus) {
        if (rosterFileProcessStatus == RosterFileProcessStatus.FAILED) {
            return RosterFileProcessStage.UNKNOWN;
        }
        List<RAConvStatusStageMappingInfo> allRosterConvMappingInfoList = getAllRosterConvMappingInfoList();
        Optional<RAConvStatusStageMappingInfo> rosterConvStatusStageMappingInfoOptional = allRosterConvMappingInfoList.stream()
                .filter(p -> p.getProcessingStatus() == rosterFileProcessStatus).findAny();
        return rosterConvStatusStageMappingInfoOptional.isPresent() ? rosterConvStatusStageMappingInfoOptional.get().getStage() : RosterFileProcessStage.UNKNOWN;
    }

    public Optional<RAConvStatusStageMappingInfo> getRosterConvStatusStageMappingInfo(RosterFileProcessStatus rosterFileProcessStatus) {
        List<RAConvStatusStageMappingInfo> RAConvStatusStageMappingInfoList = getAllRosterConvMappingInfoList();
        return RAConvStatusStageMappingInfoList.stream()
                .filter(p -> p.getProcessingStatus() == rosterFileProcessStatus).findAny();
    }

    public List<RAConvStatusStageMappingInfo> getAllRosterConvMappingInfoList(RosterFileProcessStage rosterFileProcessStage) {
        List<RAConvStatusStageMappingInfo> RAConvStatusStageMappingInfoList = getAllRosterConvMappingInfoList();
        return RAConvStatusStageMappingInfoList.stream().filter(p -> p.getStage() == rosterFileProcessStage).collect(Collectors.toList());
    }

    public List<RosterFileProcessStage> getPrecedingRosterFileProcessStageList(RosterFileProcessStage rosterFileProcessStage) {
        return Arrays.stream(RosterFileProcessStage.values()).filter(p -> p.rank <= rosterFileProcessStage.rank)
                .collect(Collectors.toList());
    }

    public Map<RosterFileProcessStatus, RosterFileProcessStage> getRosterFileStatusStagingMap() {
        List<RAConvStatusStageMappingInfo> allRosterConvMappingInfoList = getAllRosterConvMappingInfoList();
        Map<RosterFileProcessStatus, RosterFileProcessStage> rosterFileStatusStagingMap = new HashMap<>();
        for (RAConvStatusStageMappingInfo RAConvStatusStageMappingInfo : allRosterConvMappingInfoList) {
            rosterFileStatusStagingMap.put(RAConvStatusStageMappingInfo.getProcessingStatus(), RAConvStatusStageMappingInfo.getStage());
        }
        return rosterFileStatusStagingMap;
    }

    public Optional<RAConvStatusStageMappingInfo> getRosterConvStatusStageMappingInfoForStatus(RosterFileProcessStatus rosterFileProcessStatus) {
        List<RAConvStatusStageMappingInfo> allRosterConvMappingInfoList = getAllRosterConvMappingInfoList();
        return allRosterConvMappingInfoList.stream()
                .filter(p -> p.getProcessingStatus() == rosterFileProcessStatus).findFirst();
    }
}
