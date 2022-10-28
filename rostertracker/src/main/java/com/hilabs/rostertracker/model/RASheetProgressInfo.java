package com.hilabs.rostertracker.model;

import lombok.Data;

@Data
public class RASheetProgressInfo {
    private long raSheetDetailsId;
    private String sheetName;
    private String standardizedFileName;
    private long receivedTime;

    private AutoMappedStageInfo autoMapped;

    private ISFStageInfo isf;
    private ConvertedDartStageInfo convertedDart;
    private SpsLoadStageInfo spsLoad;

    public RASheetProgressInfo(long raSheetDetailsId, String sheetName, String standardizedFileName, long receivedTime) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.standardizedFileName = standardizedFileName;
        this.receivedTime = receivedTime;
        this.autoMapped = new AutoMappedStageInfo();
        this.isf = new ISFStageInfo();
        this.convertedDart = new ConvertedDartStageInfo();
        this.spsLoad = new SpsLoadStageInfo();
    }

    public RASheetProgressInfo(long raSheetDetailsId, String sheetName,
                               String standardizedFileName,
                               long receivedTime,
                               AutoMappedStageInfo autoMapped,
                               ISFStageInfo isf,
                               ConvertedDartStageInfo convertedDart,
                               SpsLoadStageInfo spsLoad) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.standardizedFileName = standardizedFileName;
        this.receivedTime = receivedTime;
        this.autoMapped = autoMapped;
        this.isf = isf;
        this.convertedDart = convertedDart;
        this.spsLoad = spsLoad;
    }
}