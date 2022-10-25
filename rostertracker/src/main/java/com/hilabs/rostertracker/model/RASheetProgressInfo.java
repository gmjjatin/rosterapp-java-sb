package com.hilabs.rostertracker.model;

import com.hilabs.rostertracker.dto.ErrorSummaryElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RASheetProgressInfo {
    private long raSheetDetailsId;
    private String sheetName;
    private String standardizedFileName;

    private RosterReceivedStageInfo rosterReceived;
    private AutoMappedStageInfo autoMapped;

    private ISFStageInfo isf;
    private ConvertedDartStageInfo convertedDart;
    private SpsLoadStageInfo spsLoad;

    private List<ErrorSummaryElement> errorSummary;

    public RASheetProgressInfo(long raSheetDetailsId, String sheetName, String standardizedFileName, long receivedTime) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.standardizedFileName = standardizedFileName;
        this.rosterReceived = new RosterReceivedStageInfo(receivedTime);
        this.autoMapped = new AutoMappedStageInfo();
        this.isf = new ISFStageInfo();
        this.convertedDart = new ConvertedDartStageInfo();
        this.spsLoad = new SpsLoadStageInfo();
        this.errorSummary = new ArrayList<>();
    }

    public RASheetProgressInfo(long raSheetDetailsId, String sheetName,
                               String standardizedFileName,
                               RosterReceivedStageInfo rosterReceived,
                               AutoMappedStageInfo autoMapped,
                               ISFStageInfo isf,
                               ConvertedDartStageInfo convertedDart,
                               SpsLoadStageInfo spsLoad, List<ErrorSummaryElement> errorSummary) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.standardizedFileName = standardizedFileName;
        this.rosterReceived = rosterReceived;
        this.autoMapped = autoMapped;
        this.isf = isf;
        this.convertedDart = convertedDart;
        this.spsLoad = spsLoad;
        this.errorSummary = errorSummary;
    }
}
