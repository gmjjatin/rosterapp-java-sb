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
            new RAStatusEntity(111, AUTO_MAPPED, "Roster Sheet Processing not Required", true, false),
            new RAStatusEntity(119, AUTO_MAPPED, "Roster Sheet Need to be Processed Manually", true, false),
            new RAStatusEntity(117, AUTO_MAPPED, "Roster Sheet Normalization Incompatible", true, false),
            new RAStatusEntity(113, AUTO_MAPPED, "Roster Sheet Normalization Completed", true, false),
            new RAStatusEntity(115, AUTO_MAPPED, "Roster Sheet Normalization Failed", true, true),
            new RAStatusEntity(121, AUTO_MAPPED, "Pre Normalization Column Mapping In Progress", false, false),
            new RAStatusEntity(123, AUTO_MAPPED, "Pre Normalization Column Mapping Failed", true, true),
            new RAStatusEntity(125, AUTO_MAPPED, "Pre Normalization Column Mapping Completed", true, false),
            new RAStatusEntity(130, AUTO_MAPPED, "Post Column Mapping Normalization In Process", false, false),
            new RAStatusEntity(131, AUTO_MAPPED, "Post Column Mapping Normalization processing Not required", true, false),
            new RAStatusEntity(133, AUTO_MAPPED, "Post Column Mapping Normalization Failed", true, true),
            new RAStatusEntity(135, AUTO_MAPPED, "Post Column Mapping Normalization Completed", true, false),
            new RAStatusEntity(139, AUTO_MAPPED, "Post Column Mapping Normalization Manual action", true, false),
            new RAStatusEntity(137, AUTO_MAPPED, "Post Column Mapping Normalization Incompatible", true, false),
            new RAStatusEntity(141, AUTO_MAPPED, "Post Normalization Column Mapping In Progress", false, false),
            new RAStatusEntity(143, AUTO_MAPPED, "Post Normalization Column Mapping Failed", true, true),
            new RAStatusEntity(145, AUTO_MAPPED, "Post Normalization Column Mapping Completed", true, false),

            new RAStatusEntity(151, ISF_GENERATED, "ISF Conversion In Progress", false, false),
            new RAStatusEntity(153, ISF_GENERATED, "ISF Conversion Failed", true, true),
            new RAStatusEntity(155, ISF_GENERATED, "ISF Conversion Completed", true, false),

            new RAStatusEntity(161, CONVERTED_DART, "ISF to DART Conversion In Progress", false, false),
            new RAStatusEntity(163, CONVERTED_DART, "ISF to DART Conversion Failed", true, true),
            new RAStatusEntity(165, CONVERTED_DART, "ISF to DART Conversion Completed", true, false)
        );

    public static List<RAStatusEntity> fileStatusEntities = Arrays.asList(
            new RAStatusEntity(13, ROSTER_RECEIVED, "Roster Ingestion in Progress", false, false),
            new RAStatusEntity(15, ROSTER_RECEIVED, "Roster Initial Validation Failed", true, true),
            new RAStatusEntity(17, ROSTER_RECEIVED, "Roster Ingestion Failed", true, true),
            new RAStatusEntity(19, ROSTER_RECEIVED, "Roster Received", true, false),

            new RAStatusEntity(21, AUTO_MAPPED, "Roster Normalization/Preprocesing in Progress", false, false),
            new RAStatusEntity(23, AUTO_MAPPED, "Roster Normalization/Preprocesing Failed Incompatible File", true, true),
            new RAStatusEntity(25, AUTO_MAPPED, "Roster Normalization/Preprocesing Failed", true, true),
            new RAStatusEntity(29, AUTO_MAPPED, "Roster Does Not require Processing", true, false),
            new RAStatusEntity(27, AUTO_MAPPED, "Roster Normalization/Preprocesing Completed", true, false),

            new RAStatusEntity(31, ISF_GENERATED, "Roster ISF Generation in Progress", false, false),
            new RAStatusEntity(33, ISF_GENERATED, "Roster ISF Generation Failed", true, true),
            new RAStatusEntity(35, ISF_GENERATED, "Roster ISF Generation Completed", true, false),

            new RAStatusEntity(41, CONVERTED_DART, "Roster DART Generation in Progress", false, false),
            new RAStatusEntity(43, CONVERTED_DART, "Roster DART Generation Failed", true, true),
            new RAStatusEntity(45, CONVERTED_DART, "Roster DART Generation Completed", false, false),
            new RAStatusEntity(51, CONVERTED_DART, "Roster DART UI Validation in Progress", false, false),
            new RAStatusEntity(53, CONVERTED_DART, "Roster DART UI Validation Failed", true, true),
            new RAStatusEntity(55, CONVERTED_DART, "Roster DART UI Validation Completed", true, false)
        );
}
