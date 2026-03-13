package com.payrolllab.service;

import com.payrolllab.dto.DashboardData;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final JdbcTemplate jdbcTemplate;
    
    public DashboardData getDashboardData() {
        DashboardData data = new DashboardData();
        
        // Total payroll cost
        String totalCostQuery = "SELECT SUM(pr.net_salary) FROM payroll_results pr";
        BigDecimal totalCost = jdbcTemplate.queryForObject(totalCostQuery, BigDecimal.class);
        data.setTotalPayrollCost(totalCost != null ? totalCost : BigDecimal.ZERO);
        
        // Total employees
        String totalEmpQuery = "SELECT COUNT(*) FROM employees";
        Long totalEmp = jdbcTemplate.queryForObject(totalEmpQuery, Long.class);
        data.setTotalEmployees(totalEmp != null ? totalEmp : 0L);

        String totaDeptsQuery = "select count(distinct department) from employees";
        Long totalDept = jdbcTemplate.queryForObject(totaDeptsQuery, Long.class);
        data.setTotalDepartments(totalDept!= null ? totalDept:0L);
        
        // Headcount by country
        String headcountQuery = "SELECT e.country, COUNT(*) as count FROM employees e GROUP BY e.country";
        List<Map<String, Object>> headcountRows = jdbcTemplate.queryForList(headcountQuery);
        Map<String, Long> headcountMap = new LinkedHashMap<>();
        headcountRows.forEach(row -> 
            headcountMap.put((String) row.get("country"), ((Number) row.get("count")).longValue()));
        data.setHeadcountByCountry(headcountMap);
        
        // Payroll by department
        String deptQuery = """
            SELECT e.department, SUM(pr.net_salary) as total
            FROM payroll_results pr
            JOIN employees e ON pr.employee_id = e.id
            GROUP BY e.department
            """;
        List<Map<String, Object>> deptRows = jdbcTemplate.queryForList(deptQuery);
        Map<String, BigDecimal> deptMap = new LinkedHashMap<>();
        deptRows.forEach(row -> 
            deptMap.put((String) row.get("department"), (BigDecimal) row.get("total")));
        data.setPayrollByDepartment(deptMap);
        
        // Payroll by country
        String countryQuery = """
            SELECT e.country, SUM(pr.net_salary) as total
            FROM payroll_results pr
            JOIN employees e ON pr.employee_id = e.id
            GROUP BY e.country
            """;
        List<Map<String, Object>> countryRows = jdbcTemplate.queryForList(countryQuery);
        Map<String, BigDecimal> countryMap = new LinkedHashMap<>();
        countryRows.forEach(row -> 
            countryMap.put((String) row.get("country"), (BigDecimal) row.get("total")));
        data.setPayrollByCountry(countryMap);
        
        return data;
    }
}
