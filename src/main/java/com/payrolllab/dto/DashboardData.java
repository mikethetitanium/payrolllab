package com.payrolllab.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class DashboardData {
    private BigDecimal totalPayrollCost;
    private Long totalEmployees;
    private Map<String, Long> headcountByCountry;
    private Map<String, BigDecimal> payrollByDepartment;
    private Map<String, BigDecimal> payrollByCountry;
    private Long totalDepartments;
}
