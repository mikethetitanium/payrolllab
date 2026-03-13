package com.payrolllab.service;

import com.payrolllab.dto.ReportData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Automated report generation service
 * Demonstrates how payroll reports can be scheduled and generated automatically
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportAutomationService {
    
    private final ReportBuilderService reportBuilderService;
    private final ExportService exportService;
    
    /**
     * Runs every day at 2 AM (in production)
     * For demo purposes, this is commented out to avoid console spam
     */
    // @Scheduled(cron = "0 0 2 * * ?")
    public void generateMonthlyPayrollReports() {
        log.info("Starting automated monthly payroll report generation...");
        
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String[] countries = {"Kenya", "UK", "Germany", "UAE"};
        
        for (String country : countries) {
            try {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("country", country);
                parameters.put("payroll_month", currentMonth);
                
                ReportData report = reportBuilderService.buildReport("payroll-summary", parameters);
                byte[] excelData = exportService.exportToExcel(report);
                
                // In production, this would:
                // - Save to file system
                // - Upload to cloud storage
                // - Email to stakeholders
                // - Archive in document management system
                
                log.info("Generated payroll report for {} - {} ({} rows)", 
                        country, currentMonth, report.getRows().size());
                
            } catch (Exception e) {
                log.error("Failed to generate report for country: {}", country, e);
            }
        }
        
        log.info("Automated report generation complete!");
    }
    
    /**
     * Generates country summary reports
     * Can be triggered manually or scheduled
     */
    public void generateCountrySummaries() {
        log.info("Generating country summary reports...");
        
        String[] countries = {"Kenya", "UK", "Germany", "UAE"};
        
        for (String country : countries) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("country", country);
            
            ReportData report = reportBuilderService.buildReport("country-totals", parameters);
            log.info("Country summary for {}: {} records", country, report.getRows().size());
        }
    }
}
