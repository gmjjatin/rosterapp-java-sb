package com.hilabs.rostertracker.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
public class RosterConfig implements Serializable {

    private static final long serialVersionUID = -2650185165626007488L;

    @Value("${downloadFolder}")
    private String downloadFolder;

    @Value("${templatesFolder}")
    private String templatesFolder;
}
