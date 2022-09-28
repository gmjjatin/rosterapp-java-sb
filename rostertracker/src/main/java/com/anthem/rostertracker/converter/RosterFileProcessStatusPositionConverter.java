package com.anthem.rostertracker.converter;

import com.anthem.rostertracker.model.RosterFileProcessStage;
import com.anthem.rostertracker.model.RosterFileProcessStatusPosition;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

import static com.anthem.rostertracker.utils.Utils.compareAlphanumeric;

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
                .filter(c -> compareAlphanumeric(c.toString(), stage.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}