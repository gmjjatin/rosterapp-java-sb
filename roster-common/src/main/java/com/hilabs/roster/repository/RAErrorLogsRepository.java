package com.hilabs.roster.repository;
import com.hilabs.roster.entity.RAErrorLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface RAErrorLogsRepository extends JpaRepository<RAErrorLogs, Long> {
}

