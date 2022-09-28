package com.anthem.rostertracker.repository;

import com.anthem.rostertracker.entity.RAFileDetails;
import com.anthem.rostertracker.entity.RASheetDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface RASheetDetailsRepository extends JpaRepository<RASheetDetails, Long> {
    @Query(value = "select * from ra_sheet_details where ra_file_details_id in (:raFileDetailsIds)", nativeQuery = true)
    List<RASheetDetails> findRASheetDetailsListForFileIdsList(List<Long> raFileDetailsIds);

}
