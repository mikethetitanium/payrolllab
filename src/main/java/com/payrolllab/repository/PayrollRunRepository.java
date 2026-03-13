package com.payrolllab.repository;

import com.payrolllab.entity.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {
    List<PayrollRun> findByCountry(String country);
    List<PayrollRun> findByPayrollMonth(String payrollMonth);
    
    @Query("SELECT DISTINCT pr.payrollMonth FROM PayrollRun pr ORDER BY pr.payrollMonth")
    List<String> findDistinctPayrollMonths();
}
