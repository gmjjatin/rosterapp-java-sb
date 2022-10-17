package com.hilabs.rostertracker.model;

import lombok.Data;

@Data
public class RASheetProgressInfo {
    private long raSheetDetailsId;
    private long receivedTime;

    private AutoMappedStageInfo autoMapped;

    private ISFStageInfo isf;
    private ConvertedDartStageInfo convertedDart;
    private SpsLoadStageInfo spsLoad;

    public RASheetProgressInfo(long raSheetDetailsId, long receivedTime) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.receivedTime = receivedTime;
        this.autoMapped = new AutoMappedStageInfo();
        this.convertedDart = new ConvertedDartStageInfo();
        this.spsLoad = new SpsLoadStageInfo();
    }

    public RASheetProgressInfo(long raSheetDetailsId, long receivedTime,
                               AutoMappedStageInfo autoMapped, ConvertedDartStageInfo convertedDart,
                               SpsLoadStageInfo spsLoad) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.receivedTime = receivedTime;
        this.autoMapped = autoMapped;
        this.convertedDart = convertedDart;
        this.spsLoad = spsLoad;
    }
}
