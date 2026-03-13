package com.payrolllab.service;

import com.payrolllab.entity.*;
import com.payrolllab.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {
    
    private final EmployeeRepository employeeRepository;
    private final PayrollRunRepository payrollRunRepository;
    private final PayrollResultRepository payrollResultRepository;
    private final DeductionRepository deductionRepository;
    private final ReportSourceRepository reportSourceRepository;
    private final ReportColumnRepository reportColumnRepository;
    private final ReportParameterRepository reportParameterRepository;
    
    private final Random random = new Random();
    
    @Override
    public void run(String... args) {
        log.info("Initializing payroll data...");
        
        // Check if data already exists
        if (employeeRepository.count() > 0) {
            log.info("Data already exists. Skipping initialization.");
            return;
        }
        
        // Create employees
        List<Employee> employees = createEmployees();
        employeeRepository.saveAll(employees);
        
        // Create payroll runs
        List<PayrollRun> payrollRuns = createPayrollRuns();
        payrollRunRepository.saveAll(payrollRuns);
        
        // Create payroll results
        List<PayrollResult> payrollResults = createPayrollResults(employees, payrollRuns);
        payrollResultRepository.saveAll(payrollResults);
        
        // Create deductions
        List<Deduction> deductions = createDeductions(employees);
        deductionRepository.saveAll(deductions);
        
        // Create report metadata
        createReportMetadata();
        
        log.info("Data initialization complete!");
    }
    
    private List<Employee> createEmployees() {
        List<Employee> employees = new ArrayList<>();
        String[] countries = {"Kenya", "UK", "Germany", "UAE"};
        String[] departments = {"Engineering", "Sales", "HR", "Finance", "Operations"};
        String[] firstNames = {"John", "Jane", "Michael", "Sarah", "David", "Emma", "James", "Olivia"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"};
        
        for (int i = 0; i < 200; i++) {
            Employee emp = new Employee();
            emp.setEmployeeName(firstNames[random.nextInt(firstNames.length)] + " " + 
                              lastNames[random.nextInt(lastNames.length)]);
            emp.setCountry(countries[random.nextInt(countries.length)]);
            emp.setDepartment(departments[random.nextInt(departments.length)]);
            emp.setHireDate(LocalDate.now().minusDays(random.nextInt(1825)));
            employees.add(emp);
        }
        
        return employees;
    }
    
    private List<PayrollRun> createPayrollRuns() {
        List<PayrollRun> runs = new ArrayList<>();
        String[] countries = {"Kenya", "UK", "Germany", "UAE"};
        String[] months = {"2025-01", "2025-02", "2025-03", "2025-04", "2025-05", "2025-06",
                          "2025-07", "2025-08", "2025-09", "2025-10", "2025-11", "2025-12"};
        
        for (String country : countries) {
            for (String month : months) {
                PayrollRun run = new PayrollRun();
                run.setPayrollMonth(month);
                run.setCountry(country);
                run.setRunDate(LocalDate.parse(month + "-28"));
                runs.add(run);
            }
        }
        
        return runs;
    }
    
    private List<PayrollResult> createPayrollResults(List<Employee> employees, List<PayrollRun> payrollRuns) {
        List<PayrollResult> results = new ArrayList<>();
        
        for (Employee emp : employees) {
            for (PayrollRun run : payrollRuns) {
                if (emp.getCountry().equals(run.getCountry())) {
                    PayrollResult result = new PayrollResult();
                    result.setEmployee(emp);
                    result.setPayrollRun(run);
                    
                    BigDecimal grossSalary = BigDecimal.valueOf(3000 + random.nextInt(7000));
                    BigDecimal taxRate = BigDecimal.valueOf(0.20 + random.nextDouble() * 0.10);
                    BigDecimal pensionRate = BigDecimal.valueOf(0.05);
                    
                    BigDecimal taxAmount = grossSalary.multiply(taxRate).setScale(2, java.math.RoundingMode.HALF_UP);
                    BigDecimal pension = grossSalary.multiply(pensionRate).setScale(2, java.math.RoundingMode.HALF_UP);
                    BigDecimal netSalary = grossSalary.subtract(taxAmount).subtract(pension);
                    
                    result.setGrossSalary(grossSalary);
                    result.setTaxAmount(taxAmount);
                    result.setPension(pension);
                    result.setNetSalary(netSalary);
                    
                    results.add(result);
                }
            }
        }
        
        return results;
    }
    
    private List<Deduction> createDeductions(List<Employee> employees) {
        List<Deduction> deductions = new ArrayList<>();
        String[] deductionTypes = {"Health Insurance", "Life Insurance", "Loan Repayment", "Union Dues"};
        
        for (Employee emp : employees) {
            if (random.nextBoolean()) {
                Deduction deduction = new Deduction();
                deduction.setEmployee(emp);
                deduction.setDeductionType(deductionTypes[random.nextInt(deductionTypes.length)]);
                deduction.setAmount(BigDecimal.valueOf(50 + random.nextInt(200)));
                deductions.add(deduction);
            }
        }
        
        return deductions;
    }
    
    private void createReportMetadata() {
        createPayrollSummaryReport();
        createEmployeePayslipReport();
        createDepartmentCostReport();
        createCountryTotalsReport();
    }
    
    private void createPayrollSummaryReport() {
        ReportSource report = new ReportSource();
        report.setRptName("payroll-summary");
        report.setDescription("Monthly Payroll Summary Report");
        report.setBaseQuery("""
            SELECT 
                e.employee_name,
                e.country,
                e.department,
                pr.payroll_month,
                pres.gross_salary,
                pres.tax_amount,
                pres.pension,
                pres.net_salary
            FROM payroll_results pres
            JOIN employees e ON pres.employee_id = e.id
            JOIN payroll_runs pr ON pres.payroll_run_id = pr.id
            """);
        reportSourceRepository.save(report);
        
        String[] columns = {"employee_name", "country", "department", "payroll_month", 
                           "gross_salary", "tax_amount", "pension", "net_salary"};
        String[] aliases = {"Employee", "Country", "Department", "Month", 
                           "Gross", "Tax", "Pension", "Net"};
        
        for (int i = 0; i < columns.length; i++) {
            ReportColumn col = new ReportColumn();
            col.setRptSrcId(report.getRptSrcId());
            col.setRptColName(columns[i]);
            col.setRptColAlias(aliases[i]);
            col.setRptColOrder(i + 1);
            col.setRptColDataType("VARCHAR");
            reportColumnRepository.save(col);
        }
    }
    
    private void createEmployeePayslipReport() {
        ReportSource report = new ReportSource();
        report.setRptName("employee-payslip");
        report.setDescription("Individual Employee Payslip");
        report.setBaseQuery("""
            SELECT 
                e.employee_name,
                e.country,
                e.department,
                pr.payroll_month,
                pres.gross_salary,
                pres.tax_amount,
                pres.pension,
                pres.net_salary
            FROM payroll_results pres
            JOIN employees e ON pres.employee_id = e.id
            JOIN payroll_runs pr ON pres.payroll_run_id = pr.id
            """);
        report = reportSourceRepository.save(report);
        
        String[] columns = {"employee_name", "country", "department", "payroll_month", 
                           "gross_salary", "tax_amount", "pension", "net_salary"};
        String[] aliases = {"Employee", "Country", "Department", "Month", 
                           "Gross", "Tax", "Pension", "Net"};
        
        for (int i = 0; i < columns.length; i++) {
            ReportColumn col = new ReportColumn();
            col.setRptSrcId(report.getRptSrcId());
            col.setRptColName(columns[i]);
            col.setRptColAlias(aliases[i]);
            col.setRptColOrder(i + 1);
            col.setRptColDataType("VARCHAR");
            reportColumnRepository.save(col);
        }
    }
    
    private void createDepartmentCostReport() {
        ReportSource report = new ReportSource();
        report.setRptName("department-cost");
        report.setDescription("Department Payroll Cost Analysis");
        report.setBaseQuery("""
            SELECT 
                e.department,
                e.country,
                COUNT(DISTINCT e.id) as employee_count,
                SUM(pres.gross_salary) as total_gross,
                SUM(pres.net_salary) as total_net
            FROM payroll_results pres
            JOIN employees e ON pres.employee_id = e.id
            JOIN payroll_runs pr ON pres.payroll_run_id = pr.id
            GROUP BY e.department, e.country
            """);
        report = reportSourceRepository.save(report);
        
        String[] columns = {"department", "country", "employee_count", "total_gross", "total_net"};
        String[] aliases = {"Department", "Country", "Employees", "Total Gross", "Total Net"};
        
        for (int i = 0; i < columns.length; i++) {
            ReportColumn col = new ReportColumn();
            col.setRptSrcId(report.getRptSrcId());
            col.setRptColName(columns[i]);
            col.setRptColAlias(aliases[i]);
            col.setRptColOrder(i + 1);
            col.setRptColDataType("VARCHAR");
            reportColumnRepository.save(col);
        }
    }
    
    private void createCountryTotalsReport() {
        ReportSource report = new ReportSource();
        report.setRptName("country-totals");
        report.setDescription("Country Payroll Totals");
        report.setBaseQuery("""
            SELECT 
                e.country,
                pr.payroll_month,
                COUNT(DISTINCT e.id) as employee_count,
                SUM(pres.gross_salary) as total_gross,
                SUM(pres.tax_amount) as total_tax,
                SUM(pres.pension) as total_pension,
                SUM(pres.net_salary) as total_net
            FROM payroll_results pres
            JOIN employees e ON pres.employee_id = e.id
            JOIN payroll_runs pr ON pres.payroll_run_id = pr.id
            GROUP BY e.country, pr.payroll_month
            """);
        report = reportSourceRepository.save(report);
        
        String[] columns = {"country", "payroll_month", "employee_count", "total_gross", "total_tax", "total_pension", "total_net"};
        String[] aliases = {"Country", "Month", "Employees", "Total Gross", "Total Tax", "Total Pension", "Total Net"};
        
        for (int i = 0; i < columns.length; i++) {
            ReportColumn col = new ReportColumn();
            col.setRptSrcId(report.getRptSrcId());
            col.setRptColName(columns[i]);
            col.setRptColAlias(aliases[i]);
            col.setRptColOrder(i + 1);
            col.setRptColDataType("VARCHAR");
            reportColumnRepository.save(col);
        }
    }
}
