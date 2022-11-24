package com.hilabs.rostertracker.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateStatusRequestElement {
    private long raFileDetailsId;
    private Integer statusCode;
    private List<SheetIdAndStatusInfo> sheetStatsList;
}
