package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RADLISFTemplate;
import com.hilabs.roster.entity.RARCRosterISFMap;
import com.hilabs.roster.repository.RADLISFTemplateRepository;
import com.hilabs.roster.repository.RARCRosterISFMapRepository;
import com.hilabs.rostertracker.dto.RosterColumnMappingData;
import com.hilabs.rostertracker.dto.RosterSheetColumnMappingInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RARCRosterISFMapService {
    @Autowired
    private RARCRosterISFMapRepository rarcRosterISFMapRepository;

    @Autowired
    private RADLISFTemplateRepository radlisfTemplateRepository;

    public List<RARCRosterISFMap> getActiveRARCRosterISFMapListForSheetId(Long raSheetDetailsId) {
        if (raSheetDetailsId == null) {
            return null;
        }
        return rarcRosterISFMapRepository.getRARCRosterISFMapList(raSheetDetailsId);
    }

    public List<String> getAllIsfColumnList() {
        Iterator<RADLISFTemplate> iterator = radlisfTemplateRepository.findAll().iterator();
        Set<String> allIsfColumnSet = new HashSet<>();
        while (iterator.hasNext()) {
            RADLISFTemplate radlisfTemplate = iterator.next();
            if (radlisfTemplate.getIsActive() == 0) {
                continue;
            }
            allIsfColumnSet.add(radlisfTemplate.getIsfColumnName());
        }
        return new ArrayList<>(allIsfColumnSet);
    }

    public RosterSheetColumnMappingInfo getRosterSheetColumnMappingInfoForSheetId(Long raSheetDetailsId) {
        if (raSheetDetailsId == null) {
            return null;
        }
        List<RARCRosterISFMap> rarcRosterISFMapList = getActiveRARCRosterISFMapListForSheetId(raSheetDetailsId);
        Set<String> allPossibleRosterColumnSet = new HashSet<>(rarcRosterISFMapList.stream().map(RARCRosterISFMap::getRosterColumnName).collect(Collectors.toList()));
        List<RosterColumnMappingData> rosterColumnMappingDataList = new ArrayList<>();
        for (String rosterColumnName : allPossibleRosterColumnSet) {
            List<RARCRosterISFMap> isfRarcRosterISFMapList = rarcRosterISFMapList.stream().
                    filter(p -> p.getColumnMappingRank() != null && p.getRosterColumnName().equals(rosterColumnName))
                    .sorted((l1, l2) -> {
                        if (l1.getColumnMappingRank() == l2.getColumnMappingRank()) {
                            return 0;
                        }
                        return l1.getColumnMappingRank() > l2.getColumnMappingRank() ? 1 : -1;
                    }).collect(Collectors.toList());
            List<String> isfColumnValues = new ArrayList<>();
            Set<String> alreadyAdded = new HashSet<>();
            for (RARCRosterISFMap rarcRosterISFMap : isfRarcRosterISFMapList) {
                if (alreadyAdded.contains(rarcRosterISFMap.getIsfColumnName())) {
                    continue;
                }
                isfColumnValues.add(rarcRosterISFMap.getIsfColumnName());
                alreadyAdded.add(rarcRosterISFMap.getIsfColumnName());
            }
            for (String isfColumn : getAllIsfColumnList()) {
                if (alreadyAdded.contains(isfColumn)) {
                    continue;
                }
                isfColumnValues.add(isfColumn);
                alreadyAdded.add(isfColumn);
            }
            rosterColumnMappingDataList.add(new RosterColumnMappingData(rosterColumnName, isfColumnValues));
        }

        return new RosterSheetColumnMappingInfo(raSheetDetailsId, rosterColumnMappingDataList);
    }

    //TODO improve or refactor
    public void updateSheetMapping(List<RARCRosterISFMap> raRCRosterISFMapList, Map<String, String> data, Long raSheetDetailsId) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String columnName = entry.getKey();
            String selIsfColumnName = entry.getValue();
            if (columnName == null || selIsfColumnName == null) {
                continue;
            }
            List<RARCRosterISFMap> colRARCRosterISFMapList = raRCRosterISFMapList.stream()
                    .filter(p -> p.getRosterColumnName().equals(columnName)).collect(Collectors.toList());
            List<RARCRosterISFMap> firstRankRARCRosterISFMapList = colRARCRosterISFMapList.stream().filter(p -> p.getColumnMappingRank() != null && p.getColumnMappingRank() == 1)
                    .collect(Collectors.toList());
            if (firstRankRARCRosterISFMapList.stream().anyMatch(p -> p.getIsfColumnName().equals(selIsfColumnName))) {
                List<Long> otherFirstRankRARCRosterISFMapIds = firstRankRARCRosterISFMapList.stream()
                        .filter(p -> !p.getIsfColumnName().equals(selIsfColumnName)).map(p -> p.getId())
                        .distinct()
                        .collect(Collectors.toList());
                if (otherFirstRankRARCRosterISFMapIds.size() > 0) {
                    rarcRosterISFMapRepository.updateIsActiveForRARCRosterISFMap(otherFirstRankRARCRosterISFMapIds, 0);
                }
            } else {
                List<Long> otherFirstRankRARCRosterISFMapIds = firstRankRARCRosterISFMapList.stream()
                        .map(p -> p.getId()).distinct().collect(Collectors.toList());
                if (otherFirstRankRARCRosterISFMapIds.size() > 0) {
                    rarcRosterISFMapRepository.updateIsActiveForRARCRosterISFMap(otherFirstRankRARCRosterISFMapIds, 0);
                }
                RARCRosterISFMap rarcRosterISFMap = new RARCRosterISFMap(raSheetDetailsId, columnName, selIsfColumnName,
                        1, 1);
                rarcRosterISFMapRepository.save(rarcRosterISFMap);
            }

        }
    }
}
