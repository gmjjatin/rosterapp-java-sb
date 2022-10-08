package com.hilabs.roster.service;

import com.hilabs.roster.entity.DartRASystemErrors;
import com.hilabs.roster.repository.DartRASystemErrorsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DartRASystemErrorsService {
    @Autowired
    private DartRASystemErrorsRepository dartRASystemErrorsRepository;

    public void saveDartRASystemErrors(final Long raFileDetailsId, final Long raSheetDetailsId, final String lastStage, final String lastStatus,
                                       final String ERROR_CATEGORY, final String ERROR_DESCRIPTION, final String ERROR_STACK_TRACE, final Integer isActive) {
        DartRASystemErrors raSystemErrors = new DartRASystemErrors(raFileDetailsId, raSheetDetailsId, lastStage, lastStatus,
                ERROR_CATEGORY, ERROR_DESCRIPTION, ERROR_STACK_TRACE, isActive);
        dartRASystemErrorsRepository.save(raSystemErrors);
    }
}
