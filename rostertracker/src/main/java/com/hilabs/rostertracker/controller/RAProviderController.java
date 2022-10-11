package com.hilabs.rostertracker.controller;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.rostertracker.service.RAFileDetailsService;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ConcurrentLruCache;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hilabs.rostertracker.service.RAFileDetailsService.getStatusCodes;

@RestController
@RequestMapping("/api/v1/ra-provider")
@Log4j2
@CrossOrigin(origins = "*")
public class RAProviderController {
    @Autowired
    RAFileDetailsService raFileDetailsService;

    public ConcurrentLruCache<String, List<RAFileDetails>> raProviderListFromSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raFileDetailsService.getRAFileDetailsListFromSearchStr(p);
    });



    @GetMapping("/file/search")
    //TODO don;t use entites
    public ResponseEntity<List<RAFileDetails>> getFileDetailsFromSearchStr(@RequestParam(defaultValue = "") String searchStr,
                                                                           @RequestParam(defaultValue = "", name = "type") String type) {
        try {
            List<RAFileDetails> raFileDetailsList = raFileDetailsService.findByFileSearchStr(searchStr);
            List<Integer> statusCodes = getStatusCodes(type);
            raFileDetailsList = raFileDetailsList.stream().filter(p -> {
                if (p.getStatusCode() == null) {
                    return false;
                }
                Integer statusCode = p.getStatusCode();
                return statusCodes.stream().anyMatch(sC -> sC.equals(statusCode));
            }).collect(Collectors.toList());
            return new ResponseEntity<>(raFileDetailsList, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvListFromProviderSearchStr searchStr {} - ex {}", searchStr, ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/market/all")
    public ResponseEntity<List<String>> getAllMarkets(@RequestParam(defaultValue = "", name = "type") String type) {
        try {
            List<Integer> statusCodes = getStatusCodes(type);
            List<String> markets = raFileDetailsService.findAllMarkets(statusCodes);
            return new ResponseEntity<>(markets, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getAllMarkets ex {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/line-of-business/all")
    public ResponseEntity<List<String>> getAllLineOfBusiness() {
        try {
            List<String> allLineOfBusiness = raFileDetailsService.findAllLineOfBusiness();
            List<String> flattenedList = new ArrayList<>();
            for (String lineOfBusiness : allLineOfBusiness) {
                String[] parts = lineOfBusiness.split(",");
                for (String part : parts) {
                    if (part != null && !part.trim().isEmpty()) {
                        flattenedList.add(part.trim());
                    }
                }
            }
            flattenedList = Utils.removeDuplicatesFromList(flattenedList);
            return new ResponseEntity<>(flattenedList, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getAllLineOfBusiness ex {}", ex.getMessage());
            throw ex;
        }
    }
}