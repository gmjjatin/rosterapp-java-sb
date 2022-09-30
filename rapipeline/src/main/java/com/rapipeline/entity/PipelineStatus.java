package com.rapipeline.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ra_file_details")
@Data
public class PipelineStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "status_code")
    private int statusCode;

    @Column(name = "dscrptn")
    private String description;

    @Column(name = "stage")
    private String stage;

    public PipelineStatus() {}

    @Override
    public String toString() {
        return "PipelineStatus [id=" + id + "]";
    }
}
