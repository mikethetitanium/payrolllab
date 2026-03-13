package com.payrolllab.repository;

import com.payrolllab.entity.ReportColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportColumnRepository extends JpaRepository<ReportColumn, Long> {
    List<ReportColumn> findByRptSrcIdOrderByRptColOrder(Long rptSrcId);
}
