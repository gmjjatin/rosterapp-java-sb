package com.hilabs.rostertracker.controller.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UpdateColumnMappingRequest implements Serializable {
    private Long raFileDetailsId;
    private List<UpdateColumnMappingSheetData> sheetDataList;
}
