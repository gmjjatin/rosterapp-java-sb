package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PreprocessingService {
    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;

    public static boolean hasIntersection(List<Integer> list1, List<Integer> list2) {
        return list1.stream().anyMatch(l1 -> list2.stream().anyMatch(l2 -> Objects.equals(l1, l2)));
    }

    public static boolean isSubset(List<Integer> list1, List<Integer> list2) {
        if (list1.size() == 0) {
            return false;
        }
        return list1.stream().allMatch(l1 -> list2.stream().anyMatch(l2 -> Objects.equals(l1, l2)));
    }

    public boolean checkCompatibleOrNotAndUpdateFileStatus(Long raFileDetailsId) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
        log.info("checkCompatibleOrNotAndUpdateFileStatus for raFileDetailsId {} raSheetDetailsList {}", raFileDetailsId,
                new Gson().toJson(raSheetDetailsList));
        List<Integer> sheetCodes = raSheetDetailsList.stream().map(s -> s.getStatusCode()).collect(Collectors.toList());
        if (sheetCodes.stream().anyMatch(Objects::isNull)) {
            log.error("One of the status codes is null for raFileDetailsId {}", raFileDetailsId);
            //TODO
            return false;
        }
        if (sheetCodes.size() == 0) {
            log.error("Zero status codes for raFileDetailsId {}", raFileDetailsId);
            //TODO
            return false;
        }
        
        if (sheetCodes.stream().anyMatch(p -> p == 117 || p == 137)) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 23);
            return false;
        } else if (hasIntersection(Arrays.asList(115, 123, 133, 143), sheetCodes)) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 25);
            return false;
        } else if (isSubset(sheetCodes, Arrays.asList(111, 119, 131, 139))) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 29);
            return false;
        } else if (isSubset(sheetCodes, Arrays.asList(111, 119, 131, 145, 139))) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 27);
            return false;
        }
        return true;
    }
}
