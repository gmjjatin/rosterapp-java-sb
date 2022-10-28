package com.hilabs.rostertracker.dto;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RAFileDetailsWithSheets {
    private RAFileDetails raFileDetails;
    private List<RASheetDetails> raSheetDetailsList;
}
