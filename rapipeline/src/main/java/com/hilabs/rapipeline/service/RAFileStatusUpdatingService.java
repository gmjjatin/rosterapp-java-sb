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
}
