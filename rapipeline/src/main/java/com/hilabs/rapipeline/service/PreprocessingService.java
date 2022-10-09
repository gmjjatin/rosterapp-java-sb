package com.hilabs.rapipeline.service;

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
        return list1.stream().allMatch(l1 -> list2.stream().anyMatch(l2 -> Objects.equals(l1, l2)));
    }

    public static class StatusCheckInfo {
        public List<Integer> statusCodes;
        public Integer fileStatusCode;
        public StatusCheckInfo(List<Integer> statusCodes, Integer fileStatusCode) {
            this.statusCodes = statusCodes;
            this.fileStatusCode = fileStatusCode;
        }
    }

    public boolean checkCompatibleOrNotAndUpdateFileStatus(Long raFileDetailsId) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
        List<Integer> sheetCodes = raSheetDetailsList.stream().map(s -> s.getStatusCode()).collect(Collectors.toList());
        HashSet<Integer> sheetCodesSet = new HashSet<>(sheetCodes);
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
        } else if (hasIntersection(Arrays.asList(115, 123, 133, 143 ,153, 163), sheetCodes)) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 30);
            return false;
        } else if (isSubset(sheetCodes, Arrays.asList(119, 111, 131, 139))) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 29);
            return false;
        } else if (isSubset(sheetCodes, Arrays.asList(111, 119, 113, 131, 133, 135, 139))) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 27);
            return false;
        }
        return true;
    }
}
