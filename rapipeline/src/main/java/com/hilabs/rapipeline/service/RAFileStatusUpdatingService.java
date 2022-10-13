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
public class RAFileStatusUpdatingService {
    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;

    @Autowired
    private RAFileDetailsService raFileDetailsService;
    public static boolean hasIntersection(List<Integer> list1, List<Integer> list2) {
        return list1.stream().anyMatch(l1 -> list2.stream().anyMatch(l2 -> Objects.equals(l1, l2)));
    }
    public static boolean isSubset(List<Integer> list1, List<Integer> list2) {
        return list1.stream().allMatch(l1 -> list2.stream().anyMatch(l2 -> Objects.equals(l1, l2)));
    }

    public boolean checkCompatibleOrNotAndUpdateFileStatusForIsf(Long raFileDetailsId, List<RASheetDetails> raSheetDetailsList) {
        log.info("checkCompatibleOrNotAndUpdateFileStatus for raFileDetailsId {} raSheetDetailsList {}", raFileDetailsId,
                new Gson().toJson(raSheetDetailsList.stream().map(p -> p.getId())));
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
        if (hasIntersection(Arrays.asList(153), sheetCodes)) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 33);
            return false;
        } else if (isSubset(sheetCodes, Arrays.asList(111, 119, 131, 139, 155))) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 35);
            return false;
        }
        return true;
    }


    public boolean checkCompatibleOrNotAndUpdateFileStatusForDart(Long raFileDetailsId, List<RASheetDetails> raSheetDetailsList) {
        log.info("checkCompatibleOrNotAndUpdateFileStatus for raFileDetailsId {} raSheetDetailsList {}", raFileDetailsId,
                new Gson().toJson(raSheetDetailsList.stream().map(p -> p.getId())));
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
        if (hasIntersection(Arrays.asList(163), sheetCodes)) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 43);
            return false;
        } else if (isSubset(sheetCodes, Arrays.asList(111, 119, 131, 139, 165))) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 45);
            return false;
        }
        return true;
    }
}
