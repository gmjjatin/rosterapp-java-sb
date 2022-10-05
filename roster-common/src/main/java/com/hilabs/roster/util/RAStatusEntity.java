package com.hilabs.roster.util;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class RAStatusEntity {
    private int code;
    private RosterSheetProcessStage stage;
    private String description;
    private boolean isCompleted;
    private boolean isFailure;

    public static List<RAStatusEntity> statusEntities = Arrays.asList(
                new RAStatusEntity(111, ,"Roster Sheet Preprocessing in Progress", false, false),
                new RAStatusEntity(113, "Roster Sheet Preprocessing Completed", true, false),
                new RAStatusEntity(115, "Roster Sheet Preprocessing Failed", true, true),
                new RAStatusEntity(117, "Roster Sheet Preprocessing Failed Incompatible Sheet", true, true),

                new RAStatusEntity(121, "Roster Sheet AI Mapping in Progress", false, false),
                //TODO confirm
                new RAStatusEntity(123, "Roster Sheet AI Mapping Completed", false, false),
                new RAStatusEntity(125, "Roster Sheet AI Mapping Failed System Error", true, true),
                new RAStatusEntity(127, "Roster Sheet AI Mapping Failed Business Error", true, true),
                new RAStatusEntity(129, "Roster Sheet AI Mapping Manual review in Progress", false, false),
                new RAStatusEntity(131, "Roster Sheet AI Mapping Manual review Failed", true, true),
                //TODO confirm
                new RAStatusEntity(133, "Roster Sheet AI Mapping Manual review completed", false, false),
                //TODO confirm
                new RAStatusEntity(135, "Roster Sheet AI Mapping Manually updated", false, false),
                new RAStatusEntity(137, "Roster Sheet AI Mapping Post review validation in Progress", false, false),
                new RAStatusEntity(139, "Roster Sheet AI Mapping Post review validation Completed", true, true),
                new RAStatusEntity(141, "Roster Sheet AI Mapping Post review validation Failed", true, true),

                new RAStatusEntity(151, "Roster Sheet ISF generation in Progress", false, false),
                //TODO confirm
                new RAStatusEntity(153, "Roster Sheet ISF generation Completed", false, false),
                new RAStatusEntity(155, "Roster Sheet ISF generation Failed", true, true),
                new RAStatusEntity(157, "Roster Sheet ISF validation in Progress", false, false),
                new RAStatusEntity(159, "Roster Sheet ISF validation Failed", true, true),

                new RAStatusEntity(161, "Roster Sheet DART generation in Progress", false, false),
                //TODO confirm
                new RAStatusEntity(163, "Roster Sheet DART generation Completed", false, false),
                new RAStatusEntity(165, "Roster Sheet DART generation Failed", true, true),
                new RAStatusEntity(167, "Roster Sheet DART validation in Progress", false, false),
                new RAStatusEntity(169, "Roster Sheet DART validation Failed", true, true),
                new RAStatusEntity(171, "Roster Sheet DART validation Completed", true, false),

                new RAStatusEntity(181, "Roster Sheet DART UI validation in Progress", false, false),
                new RAStatusEntity(183, "Roster Sheet DART UI validation Completed", true, false),
                new RAStatusEntity(185, "Roster Sheet DART UI validation Failed", true, true),

                new RAStatusEntity(181, "Roster Sheet SPS load in Progress", false, false),
                new RAStatusEntity(183, "Roster Sheet SPS load Completed", true, false),
                new RAStatusEntity(185, "Roster Sheet SPS load Failed", true, true)
        );
}
