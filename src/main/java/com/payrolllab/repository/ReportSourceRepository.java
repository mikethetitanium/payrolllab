package com.payrolllab.repository;

import com.payrolllab.entity.ReportSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReportSourceRepository extends JpaRepository<ReportSource, Long> {
    Optional<ReportSource> findByRptName(String rptName);
}
