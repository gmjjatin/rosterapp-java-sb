package com.hilabs.rostertracker.converter;

import com.hilabs.roster.model.RosterFileProcessStatusPosition;
import com.hilabs.rostertracker.utils.Utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RosterFileProcessStatusPositionConverter implements AttributeConverter<RosterFileProcessStatusPosition, String> {

    @Override
    public String convertToDatabaseColumn(RosterFileProcessStatusPosition category) {
        if (category == null) {
            return null;
        }
        return category.toString();
    }

    @Override
    public RosterFileProcessStatusPosition convertToEntityAttribute(String stage) {
        if (stage == null) {
            return null;
        }
        return Stream.of(RosterFileProcessStatusPosition.values())
                .filter(c -> Utils.compareAlphanumeric(c.toString(), stage.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}