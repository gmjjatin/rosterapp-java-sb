package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RADLISFTemplate;
import com.hilabs.roster.entity.RARCRosterISFMap;
import com.hilabs.roster.repository.RADLISFTemplateRepository;
import com.hilabs.roster.repository.RARCRosterISFMapRepository;
import com.hilabs.rostertracker.dto.IsfColumnInfo;
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
        //TODO order by column
        List<String> columnList = new ArrayList<>(allIsfColumnSet);
        Collections.sort(columnList);
        return columnList;
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
            Integer displayOrder = isfRarcRosterISFMapList.size() > 0 ? isfRarcRosterISFMapList.get(0).getDisplayOrder() : Integer.MAX_VALUE;
            List<IsfColumnInfo> isfColumnValues = new ArrayList<>();
            Set<String> alreadyAdded = new HashSet<>();
            for (RARCRosterISFMap rarcRosterISFMap : isfRarcRosterISFMapList) {
                if (alreadyAdded.contains(rarcRosterISFMap.getIsfColumnName())) {
                    continue;
                }
                isfColumnValues.add(new IsfColumnInfo(rarcRosterISFMap.getIsfColumnName(), true));
                alreadyAdded.add(rarcRosterISFMap.getIsfColumnName());
            }
            for (String isfColumn : getAllIsfColumnList()) {
                if (alreadyAdded.contains(isfColumn)) {
                    continue;
                }
                isfColumnValues.add(new IsfColumnInfo(isfColumn, false));
                alreadyAdded.add(isfColumn);
            }
            rosterColumnMappingDataList.add(new RosterColumnMappingData(rosterColumnName, displayOrder, isfColumnValues));
        }
        rosterColumnMappingDataList.sort((l, r) -> {
            int lD = l.getDisplayOrder() == null ? Integer.MAX_VALUE : l.getDisplayOrder();
            int rD = r.getDisplayOrder() == null ? Integer.MAX_VALUE : r.getDisplayOrder();
            if (lD == rD) {
                String lStr = l.getRosterColumnName();
                String rStr = r.getRosterColumnName();
                return lStr.compareTo(rStr);
            }
            return lD < rD ? -1 : 1;
        });
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

    public int countMappingCountForSheetDetailsId(Long raSheetDetailsId) {
        return rarcRosterISFMapRepository.countMappingCountForSheetDetailsId(raSheetDetailsId);
    }
}
