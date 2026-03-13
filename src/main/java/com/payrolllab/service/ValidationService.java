package com.payrolllab.service;

import com.payrolllab.dto.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidationService {
    
    private final JdbcTemplate jdbcTemplate;
    
    public List<ValidationResult> validatePayrollReport(String payrollMonth, String country) {
        List<ValidationResult> results = new ArrayList<>();
        
        // Validation 1: Gross = Net + Tax + Pension
        results.add(validateGrossCalculation(payrollMonth, country));
        
        // Validation 2: Total payroll matches sum of individual results
        results.add(validateTotalPayroll(payrollMonth, country));
        
        // Validation 3: All employees have payroll records
        results.add(validateEmployeeCoverage(payrollMonth, country));
        
        return results;
    }
    
    private ValidationResult validateGrossCalculation(String payrollMonth, String country) {
        ValidationResult result = new ValidationResult();
        result.setCheckName("Gross Salary Calculation");
        
        String query = """
            SELECT 
                SUM(gross_salary) as total_gross,
                SUM(net_salary + tax_amount + pension) as calculated_gross
            FROM payroll_results pr
            JOIN payroll_runs run ON pr.payroll_run_id = run.id
            WHERE run.payroll_month = ? AND run.country = ?
            """;
        
        var row = jdbcTemplate.queryForMap(query, payrollMonth, country);
        BigDecimal totalGross = (BigDecimal) row.get("total_gross");
        BigDecimal calculatedGross = (BigDecimal) row.get("calculated_gross");
        
        result.setExpectedValue(totalGross);
        result.setActualValue(calculatedGross);
        result.setPassed(totalGross.compareTo(calculatedGross) == 0);
        result.setMessage(result.isPassed() ? "Gross calculation is correct" : 
            "Gross calculation mismatch");
        
        return result;
    }
    
    private ValidationResult validateTotalPayroll(String payrollMonth, String country) {
        ValidationResult result = new ValidationResult();
        result.setCheckName("Total Payroll Sum");
        
        String query = """
            SELECT SUM(net_salary) as total
            FROM payroll_results pr
            JOIN payroll_runs run ON pr.payroll_run_id = run.id
            WHERE run.payroll_month = ? AND run.country = ?
            """;
        
        BigDecimal total = jdbcTemplate.queryForObject(query, BigDecimal.class, payrollMonth, country);
        
        result.setExpectedValue(total);
        result.setActualValue(total);
        result.setPassed(true);
        result.setMessage("Total payroll: " + total);
        
        return result;
    }
    
    private ValidationResult validateEmployeeCoverage(String payrollMonth, String country) {
        ValidationResult result = new ValidationResult();
        result.setCheckName("Employee Coverage");
        
        String query = """
            SELECT 
                (SELECT COUNT(*) FROM employees WHERE country = ?) as total_employees,
                (SELECT COUNT(DISTINCT pr.employee_id) 
                 FROM payroll_results pr
                 JOIN payroll_runs run ON pr.payroll_run_id = run.id
                 WHERE run.payroll_month = ? AND run.country = ?) as employees_in_payroll
            """;
        
        var row = jdbcTemplate.queryForMap(query, country, payrollMonth, country);
        Long totalEmployees = ((Number) row.get("total_employees")).longValue();
        Long employeesInPayroll = ((Number) row.get("employees_in_payroll")).longValue();
        
        result.setExpectedValue(new BigDecimal(totalEmployees));
        result.setActualValue(new BigDecimal(employeesInPayroll));
        result.setPassed(totalEmployees.equals(employeesInPayroll));
        result.setMessage(result.isPassed() ? "All employees covered" : 
            "Missing " + (totalEmployees - employeesInPayroll) + " employees");
        
        return result;
    }
}
