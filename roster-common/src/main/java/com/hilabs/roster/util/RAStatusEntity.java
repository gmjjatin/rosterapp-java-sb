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

    public static List<RAStatusEntity> sheetStatusEntities = Arrays.asList(
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

    public static List<RAStatusEntity> fileStatusEntities = Arrays.asList(
                new RAStatusEntity(13, ROSTER_RECEIVED, "Roster Ingestion in Progress", false, false),
                new RAStatusEntity(15, ROSTER_RECEIVED, "Roster Initial Validation Failed", true, true),
                new RAStatusEntity(17, ROSTER_RECEIVED,  "Roster Ingestion Failed", true, true),
                new RAStatusEntity(19, ROSTER_RECEIVED, "Roster Received", true, false),

                new RAStatusEntity(21, PRE_PROCESSING,"Roster Preprocessing in Progress", false, false),
                new RAStatusEntity(23 ,PRE_PROCESSING, "Roster Preprocessing Completed", true, false),
                new RAStatusEntity(25 , PRE_PROCESSING,"Roster Preprocessing Failed", true, true),
                new RAStatusEntity(27 ,PRE_PROCESSING, "Roster Preprocessing Failed Incompatible File", true, true),

                new RAStatusEntity(31 ,AUTO_MAPPED, "Roster AI Mapping in Progress", false, false),
                new RAStatusEntity(33 , AUTO_MAPPED,"Roster AI Mapping Completed", false, false),
                new RAStatusEntity(35 , AUTO_MAPPED,"Roster AI Mapping Failed System Error", true, true),
                new RAStatusEntity(37 , AUTO_MAPPED,"Roster AI Mapping Failed Business Error", true, true),
                new RAStatusEntity(39 ,AUTO_MAPPED, "Roster AI Mapping Manual review in Progress", false, false),
                new RAStatusEntity(41 , AUTO_MAPPED,"Roster AI Mapping Manual review Failed", true, true),
                new RAStatusEntity(43 , AUTO_MAPPED,"Roster AI Mapping Manual review completed", false, false),
                new RAStatusEntity(45 , AUTO_MAPPED,"Roster AI Mapping Manually updated", false, false),
                new RAStatusEntity(47 , AUTO_MAPPED,"Roster AI Mapping Post review validation in Progress", false, false),
                new RAStatusEntity(49 ,AUTO_MAPPED, "Roster AI Mapping Post review validation Completed", false, false),
                new RAStatusEntity(51 ,AUTO_MAPPED, "Roster AI Mapping Post review validation Failed", true, true),

                new RAStatusEntity(61 , ISF,"Roster ISF generation in Progress", false, false),
                new RAStatusEntity(63 , ISF,"Roster ISF generation Completed", false, false),
                new RAStatusEntity(65 , ISF,"Roster ISF generation Failed", true, true),
                new RAStatusEntity(67 , ISF,"Roster ISF validation in Progress", false, false),
                new RAStatusEntity(69 , ISF,"Roster ISF validation Failed", true, true),
                new RAStatusEntity(68 , ISF,"Roster ISF validation Completed", false, false),
                new RAStatusEntity(71 , CONVERTED_DART,"Roster DART generation in Progress", false, false),
                new RAStatusEntity(73 , CONVERTED_DART,"Roster DART generation Completed", false, false),
                new RAStatusEntity(75 ,CONVERTED_DART, "Roster DART generation Failed", true, true),
                new RAStatusEntity(77 ,CONVERTED_DART, "Roster DART validation in Progress", false, false),
                new RAStatusEntity(79 ,CONVERTED_DART, "Roster DART validation Failed", true, true),
                new RAStatusEntity(81 ,CONVERTED_DART, "Roster DART validation Completed", false, false),
                new RAStatusEntity(91 ,CONVERTED_DART, "Roster DART UI validation in Progress", false, false),
                new RAStatusEntity(93 , CONVERTED_DART, "Roster DART UI validation Completed", false, false),
                new RAStatusEntity(95 ,CONVERTED_DART, "Roster DART UI validation Failed", true, true),
                new RAStatusEntity(101 , SPS_LOAD,"Roster SPS load in Progress", false, false),
                new RAStatusEntity(103 , SPS_LOAD,"Roster SPS load Completed", false, false),
                new RAStatusEntity(105 , SPS_LOAD,"Roster SPS load Failed", true, true)
        );
}
