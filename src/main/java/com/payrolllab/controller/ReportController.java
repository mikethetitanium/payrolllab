package com.payrolllab.controller;

import com.payrolllab.dto.ReportData;
import com.payrolllab.dto.ValidationResult;
import com.payrolllab.entity.ReportSource;
import com.payrolllab.repository.EmployeeRepository;
import com.payrolllab.repository.PayrollRunRepository;
import com.payrolllab.repository.ReportSourceRepository;
import com.payrolllab.service.ExportService;
import com.payrolllab.service.ReportBuilderService;
import com.payrolllab.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportBuilderService reportBuilderService;
    private final ReportSourceRepository reportSourceRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollRunRepository payrollRunRepository;
    private final ExportService exportService;
    private final ValidationService validationService;
    
    @GetMapping
    public String listReports(Model model) {
        List<ReportSource> reports = reportSourceRepository.findAll();
        model.addAttribute("reports", reports);
        return "reports/list";
    }
    
    @GetMapping("/{reportName}")
    public String viewReport(@PathVariable String reportName,
                            @RequestParam(name = "country", required = false) String country,
                            @RequestParam(name = "payrollMonth", required = false) String payrollMonth,
                            @RequestParam(name = "department", required = false) String department,
                            Model model) {
        
        Map<String, Object> parameters = new HashMap<>();
        if (country != null && !country.isEmpty()) parameters.put("country", country);
        if (payrollMonth != null && !payrollMonth.isEmpty()) parameters.put("payroll_month", payrollMonth);
        if (department != null && !department.isEmpty()) parameters.put("department", department);
        
        ReportData reportData = reportBuilderService.buildReport(reportName, parameters);
        
        model.addAttribute("report", reportData);
        model.addAttribute("countries", employeeRepository.findDistinctCountries());
        model.addAttribute("departments", employeeRepository.findDistinctDepartments());
        model.addAttribute("payrollMonths", payrollRunRepository.findDistinctPayrollMonths());
        
        return "reports/view";
    }
    
    @GetMapping("/{reportName}/export/excel")
    public ResponseEntity<byte[]> exportExcel(@PathVariable String reportName,
                                              @RequestParam(name = "country", required = false) String country,
                                              @RequestParam(name = "payrollMonth", required = false) String payrollMonth,
                                              @RequestParam(name = "department", required = false) String department) {
        
        Map<String, Object> parameters = new HashMap<>();
        if (country != null) parameters.put("country", country);
        if (payrollMonth != null) parameters.put("payroll_month", payrollMonth);
        if (department != null) parameters.put("department", department);
        
        ReportData reportData = reportBuilderService.buildReport(reportName, parameters);
        byte[] excelData = exportService.exportToExcel(reportData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportName + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
    
    @GetMapping("/{reportName}/export/csv")
    public ResponseEntity<String> exportCsv(@PathVariable String reportName,
                                           @RequestParam(name = "country", required = false) String country,
                                           @RequestParam(name = "payrollMonth", required = false) String payrollMonth,
                                           @RequestParam(name = "department", required = false) String department) {
        
        Map<String, Object> parameters = new HashMap<>();
        if (country != null) parameters.put("country", country);
        if (payrollMonth != null) parameters.put("payroll_month", payrollMonth);
        if (department != null) parameters.put("department", department);
        
        ReportData reportData = reportBuilderService.buildReport(reportName, parameters);
        String csvData = exportService.exportToCsv(reportData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportName + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
    
    @GetMapping("/validate")
    public String validateReport(@RequestParam(name = "payrollMonth") String payrollMonth,
                                 @RequestParam(name = "country") String country,
                                 Model model) {
        
        List<ValidationResult> results = validationService.validatePayrollReport(payrollMonth, country);
        model.addAttribute("validationResults", results);
        model.addAttribute("payrollMonth", payrollMonth);
        model.addAttribute("country", country);
        model.addAttribute("countries", employeeRepository.findDistinctCountries());
        model.addAttribute("payrollMonths", payrollRunRepository.findDistinctPayrollMonths());
        
        return "reports/validation";
    }
}
