package com.payrolllab.repository;

import com.payrolllab.entity.PayrollResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollResultRepository extends JpaRepository<PayrollResult, Long> {
}
