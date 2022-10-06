package com.hilabs.roster.util;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

import static com.hilabs.roster.model.RosterSheetProcessStage.*;

@Data
@AllArgsConstructor
public class RAStatusEntity {
    private int code;
    private RosterSheetProcessStage stage;
    private String description;
    private boolean isCompleted;
    private boolean isFailure;

    public static List<RAStatusEntity> statusEntities = Arrays.asList(
                new RAStatusEntity(111, PRE_PROCESSING,"Roster Sheet Preprocessing in Progress",
                        false, false),
                new RAStatusEntity(113, PRE_PROCESSING, "Roster Sheet Preprocessing Completed", true, false),
                new RAStatusEntity(115, PRE_PROCESSING, "Roster Sheet Preprocessing Failed", true, true),
                new RAStatusEntity(117,PRE_PROCESSING,  "Roster Sheet Preprocessing Failed Incompatible Sheet", true, true),

                new RAStatusEntity(121, AUTO_MAPPED,"Roster Sheet AI Mapping in Progress", false, false),
                //TODO confirm
                new RAStatusEntity(123,AUTO_MAPPED, "Roster Sheet AI Mapping Completed", false, false),
                new RAStatusEntity(125,AUTO_MAPPED, "Roster Sheet AI Mapping Failed System Error", true, true),
                new RAStatusEntity(127,AUTO_MAPPED, "Roster Sheet AI Mapping Failed Business Error", true, true),
                new RAStatusEntity(129,AUTO_MAPPED, "Roster Sheet AI Mapping Manual review in Progress", false, false),
                new RAStatusEntity(131,AUTO_MAPPED, "Roster Sheet AI Mapping Manual review Failed", true, true),
                //TODO confirm
                new RAStatusEntity(133,AUTO_MAPPED, "Roster Sheet AI Mapping Manual review completed", false, false),
                //TODO confirm
                new RAStatusEntity(135,AUTO_MAPPED, "Roster Sheet AI Mapping Manually updated", false, false),
                new RAStatusEntity(137,AUTO_MAPPED, "Roster Sheet AI Mapping Post review validation in Progress", false, false),
                new RAStatusEntity(139,AUTO_MAPPED, "Roster Sheet AI Mapping Post review validation Completed", true, true),
                new RAStatusEntity(141, AUTO_MAPPED, "Roster Sheet AI Mapping Post review validation Failed", true, true),

                new RAStatusEntity(151,ISF, "Roster Sheet ISF generation in Progress", false, false),
                //TODO confirm
                new RAStatusEntity(153,ISF, "Roster Sheet ISF generation Completed", false, false),
                new RAStatusEntity(155,ISF, "Roster Sheet ISF generation Failed", true, true),
                new RAStatusEntity(157, ISF,"Roster Sheet ISF validation in Progress", false, false),
                new RAStatusEntity(158, ISF,"Roster Sheet ISF validation Completed", true, false),
                new RAStatusEntity(159, ISF,"Roster Sheet ISF validation Failed", true, true),


                new RAStatusEntity(161,CONVERTED_DART, "Roster Sheet DART generation in Progress", false, false),
            //TODO confirm
                new RAStatusEntity(163, CONVERTED_DART,"Roster Sheet DART generation Completed", false, false),
                new RAStatusEntity(165,CONVERTED_DART, "Roster Sheet DART generation Failed", true, true),
                new RAStatusEntity(167,CONVERTED_DART, "Roster Sheet DART validation in Progress", false, false),
                new RAStatusEntity(169,CONVERTED_DART, "Roster Sheet DART validation Failed", true, true),
                new RAStatusEntity(171,CONVERTED_DART, "Roster Sheet DART validation Completed", false, false),
                new RAStatusEntity(181,CONVERTED_DART, "Roster Sheet DART UI validation in Progress", false, false),
                new RAStatusEntity(183,CONVERTED_DART, "Roster Sheet DART UI validation Completed", true, false),
                new RAStatusEntity(185,CONVERTED_DART, "Roster Sheet DART UI validation Failed", true, true),

                new RAStatusEntity(181,SPS_LOAD, "Roster Sheet SPS load in Progress", false, false),
                new RAStatusEntity(183,SPS_LOAD, "Roster Sheet SPS load Completed", true, false),
                new RAStatusEntity(185,SPS_LOAD, "Roster Sheet SPS load Failed", true, true)
        );
}
