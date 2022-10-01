package com.hilabs.rostertracker.controller;

import com.hilabs.roster.entity.RAProvDetails;
import com.hilabs.rostertracker.service.RAProviderService;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ConcurrentLruCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ra-provider")
@Log4j2
public class RAProviderController {
    @Autowired
    RAProviderService raProviderService;

    public ConcurrentLruCache<String, List<RAProvDetails>> raProviderListFromSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raProviderService.getRAProvListFromSearchStr(p);
    });

    //This searches for provider and market
    @GetMapping("/search")
    public ResponseEntity<List<RAProvDetails>> getRAProvListFromSearchStrApi(@RequestParam(defaultValue = "") String searchStr) {
        try {
            try {
                return new ResponseEntity<>(raProviderListFromSearchStrCache.get(searchStr), HttpStatus.OK);
            } catch (Exception ex) {
                return new ResponseEntity<>(raProviderService.getRAProvListFromSearchStr(searchStr), HttpStatus.OK);
            }
        } catch (Exception ex) {
            log.error("Error in getRAProvListFromSearchStrApi searchStr {} - ex {}", searchStr, ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/provider/search")
    public ResponseEntity<List<RAProvDetails>> getRAProvListFromProviderSearchStr(@RequestParam(defaultValue = "") String searchStr) {
        try {
            List<RAProvDetails> raProvDetails;
            if (searchStr == null || searchStr.isEmpty()) {
                raProvDetails = raProviderService.findTopEntriesOrderBySourceName(RAProviderService.DEFAULT_NO_OF_ENTRIES);
            } else {
                raProvDetails = raProviderService.findByProviderSearchStr(searchStr);
            }
            return new ResponseEntity<>(raProvDetails, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvListFromProviderSearchStr searchStr {} - ex {}", searchStr, ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/market/all")
    public ResponseEntity<List<String>> getAllMarkets() {
        try {
            List<String> markets = raProviderService.findAllMarkets();
            return new ResponseEntity<>(markets, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getAllMarkets ex {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/line-of-business/all")
    public ResponseEntity<List<String>> getAllLineOfBusiness() {
        try {
            List<String> allLineOfBusiness = raProviderService.findAllLineOfBusiness();
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