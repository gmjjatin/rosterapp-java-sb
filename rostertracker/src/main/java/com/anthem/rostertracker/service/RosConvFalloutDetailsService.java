//package com.anthem.rostertracker.service;
//
//import com.anthem.rostertracker.dto.RosterFileFalloutTransactionInfo;
//import com.anthem.rostertracker.model.RosterFileFalloutTransactionInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class RosConvFalloutDetailsService {
//    @Autowired
//    private RosConvFalloutDetailsRepository rosConvFalloutDetailsRepository;
//
//    public List<RosterFileFalloutTransactionInfo> getRosterFileFalloutTransactionInfoList(long rosterFileDetailsId) {
//        List<RosConvFalloutDetails> rosConvFalloutDetailsList = rosConvFalloutDetailsRepository
//                .getRosConvFalloutDetailsList(rosterFileDetailsId);
//        Map<String, RosterFileFalloutTransactionInfo> rosterFileFalloutTransactionInfoMap = new HashMap<>();
//        for (RosConvFalloutDetails rosConvFalloutDetails : rosConvFalloutDetailsList) {
//            rosterFileFalloutTransactionInfoMap.putIfAbsent(rosConvFalloutDetails.getTransactionType(),
//                    new RosterFileFalloutTransactionInfo(rosConvFalloutDetails.getTransactionType(), rosConvFalloutDetails.getErrorDescription()));
//            rosterFileFalloutTransactionInfoMap.get(rosConvFalloutDetails.getTransactionType()).increment(rosConvFalloutDetails.getTransactionStatus(),
//                    rosConvFalloutDetails.getNoOfRecords());
//        }
//        List<RosterFileFalloutTransactionInfo> rosterFileFalloutTransactionInfoList = new ArrayList<>();
//        for (Map.Entry<String, RosterFileFalloutTransactionInfo> entry : rosterFileFalloutTransactionInfoMap.entrySet()) {
//            rosterFileFalloutTransactionInfoList.add(entry.getValue());
//        }
//        return rosterFileFalloutTransactionInfoList;
//    }
//}
