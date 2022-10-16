package com.hilabs.rostertracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
public class CollectionResponse<T> {
    private Integer pageNum;
    private Integer pageSize;
    private List<T> items;
    private Long totalCount;

    
}
