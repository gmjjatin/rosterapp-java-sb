package com.hilabs.rostertracker.model;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RASheetProgressInfo {
    private long raSheetDetailsId;
    private String sheetName;
    private String standardizedFileName;
    private long receivedTime;
    private Integer sheetStatusCode;
    private Integer fileStatusCode;

    private String dartFileName;

    private RosterReceivedStageInfo rosterReceived;

    private AutoMappedStageInfo autoMapped;

    private ISFStageInfo isf;
    private ConvertedDartStageInfo convertedDart;
    private SpsLoadStageInfo spsLoad;

    private List<RAFalloutErrorInfo> errorSummary;



    public RASheetProgressInfo(long raSheetDetailsId, String sheetName, String standardizedFileName,
                               long receivedTime, String dartFileName, Integer sheetStatusCode, Integer fileStatusCode) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.standardizedFileName = standardizedFileName;
        this.receivedTime = receivedTime;
        this.sheetStatusCode = sheetStatusCode;
        this.fileStatusCode = fileStatusCode;
        this.dartFileName = dartFileName;
        this.rosterReceived = new RosterReceivedStageInfo();
        this.autoMapped = new AutoMappedStageInfo();
        this.isf = new ISFStageInfo();
        this.convertedDart = new ConvertedDartStageInfo();
        this.spsLoad = new SpsLoadStageInfo();
        this.errorSummary = new ArrayList<>();
    }
}