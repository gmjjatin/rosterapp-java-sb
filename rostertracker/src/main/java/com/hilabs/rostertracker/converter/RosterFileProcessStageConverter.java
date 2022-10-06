package com.hilabs.rostertracker.converter;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.rostertracker.utils.Utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RosterFileProcessStageConverter implements AttributeConverter<RosterSheetProcessStage, String> {

    @Override
    public String convertToDatabaseColumn(RosterSheetProcessStage category) {
        if (category == null) {
            return null;
        }
        return category.displayName;
    }

    @Override
    public RosterSheetProcessStage convertToEntityAttribute(String stage) {
        if (stage == null) {
            return null;
        }
        return Stream.of(RosterSheetProcessStage.values())
                .filter(c -> Utils.compareAlphanumeric(c.displayName.toUpperCase(), stage.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}