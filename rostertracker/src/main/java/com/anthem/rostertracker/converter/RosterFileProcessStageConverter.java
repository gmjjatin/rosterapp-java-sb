package com.anthem.rostertracker.converter;

import com.anthem.rostertracker.model.RosterFileProcessStage;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

import static com.anthem.rostertracker.utils.Utils.compareAlphanumeric;

@Converter(autoApply = true)
public class RosterFileProcessStageConverter implements AttributeConverter<RosterFileProcessStage, String> {

    @Override
    public String convertToDatabaseColumn(RosterFileProcessStage category) {
        if (category == null) {
            return null;
        }
        return category.displayName;
    }

    @Override
    public RosterFileProcessStage convertToEntityAttribute(String stage) {
        if (stage == null) {
            return null;
        }
        return Stream.of(RosterFileProcessStage.values())
                .filter(c -> compareAlphanumeric(c.displayName.toUpperCase(), stage.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}