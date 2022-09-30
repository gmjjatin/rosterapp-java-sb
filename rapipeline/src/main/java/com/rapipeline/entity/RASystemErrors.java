package com.rapipeline.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ra_system_errors")
@Data
public class RASystemErrors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_file_details_id")
    private int raFileDetailsId;

    @Column(name = "last_stage")
    private String lastStage;

    @Column(name = "last_status")
    private Integer lastStatus;

    @Column(name = "error_category")
    private String errorCategory;

    @Column(name = "error_description")
    private String errorDescription;

    @Column(name = "error_stack_trace")
    private String errorStackTrace;
}