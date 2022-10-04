package com.hilabs.rostertracker.service;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.repository.RARCFalloutReportRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class RAFalloutReportService {
    @Autowired
    RARCFalloutReportRepository raFalloutReportRepository;

    public List<RAFalloutErrorInfo> getRASheetFalloutReport(Long raSheetDetailsId) {
        //TODO rewrite code
        List<RAFalloutErrorInfo> raFalloutErrorInfoList = raFalloutReportRepository.getRAFalloutErrorInfoList(raSheetDetailsId);
        return raFalloutErrorInfoList;
    }
}
