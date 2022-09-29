package com.rapipeline.repository;

import com.rapipeline.entity.RAPlmRoFileData;
import com.rapipeline.entity.RAPlmRoProfData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RAPlmRoProfDataRepository extends JpaRepository<RAPlmRoProfData, Long> {
}