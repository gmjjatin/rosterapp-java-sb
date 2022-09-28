package com.anthem.rostertracker.service;

import com.anthem.rostertracker.dto.RAFalloutErrorInfo;
import com.anthem.rostertracker.dto.RASheetFalloutReport;
import com.anthem.rostertracker.entity.RAFalloutReport;
import com.anthem.rostertracker.repository.RAFalloutReportRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class RAFalloutReportService {
    @Autowired
    RAFalloutReportRepository raFalloutReportRepository;

    public List<RAFalloutErrorInfo> getRASheetFalloutReport(Long raSheetDetailsId) {
        //TODO rewrite code
        List<RAFalloutErrorInfo> raFalloutErrorInfoList = raFalloutReportRepository.getRAFalloutErrorInfoList(raSheetDetailsId);
        return raFalloutErrorInfoList;
    }
}
