package com.hilabs.rostertracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RosterFileProgressInfoListResponse {
    private String fileName;
    private Long fileReceivedTime;
    private String lob;
    private String market;
    private String plmTicketId;
    private Integer fileStatusCode;
    private List<RASheetProgressInfo> progressInfoList;
}
