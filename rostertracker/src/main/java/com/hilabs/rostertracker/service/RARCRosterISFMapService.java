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
    public void updateSheetMapping(List<RARCRosterISFMap> rarcRosterISFMapList, Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String columnName = entry.getKey();
            String selIsColumnName = entry.getValue();
            if (columnName == null || selIsColumnName == null) {
                continue;
            }
            List<RARCRosterISFMap> filteredRARCRosterISFMapList = rarcRosterISFMapList.stream().filter(p -> p.getRosterColumnName()
                            .equals(columnName)).collect(Collectors.toList());
            List<> a = filteredRARCRosterISFMapList.stream().filter(p -> p.getColumnMappingRank() == 1
                    || p.getRosterColumnName().equals(columnName)).collect(Collectors.toList());
            rarcRosterISFMapRepository.updateIsActiveForRARCRosterISFMap()
        }
    }
}
