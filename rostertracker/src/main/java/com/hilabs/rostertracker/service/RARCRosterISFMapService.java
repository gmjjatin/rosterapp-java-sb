package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RARCRosterISFMap;
import com.hilabs.roster.repository.RARCRosterISFMapRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RARCRosterISFMapService {
    @Autowired
    private RARCRosterISFMapRepository rarcRosterISFMapRepository;

    public List<RARCRosterISFMap> getRARCRosterISFMapListForSheetId(Long raSheetDetailsId) {
        if (raSheetDetailsId == null) {
            return null;
        }
        return rarcRosterISFMapRepository.getRARCRosterISFMapList(raSheetDetailsId);
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
