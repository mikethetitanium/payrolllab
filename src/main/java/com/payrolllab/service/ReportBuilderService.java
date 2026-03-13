package com.payrolllab.service;

import com.payrolllab.dto.ReportData;
import com.payrolllab.entity.ReportColumn;
import com.payrolllab.entity.ReportParameter;
import com.payrolllab.entity.ReportSource;
import com.payrolllab.repository.ReportColumnRepository;
import com.payrolllab.repository.ReportParameterRepository;
import com.payrolllab.repository.ReportSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportBuilderService {
    
    private final ReportSourceRepository reportSourceRepository;
    private final ReportColumnRepository reportColumnRepository;
    private final ReportParameterRepository reportParameterRepository;
    private final JdbcTemplate jdbcTemplate;
    
    public ReportData buildReport(String reportName, Map<String, Object> parameters) {
        ReportSource reportSource = reportSourceRepository.findByRptName(reportName)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportName));
        
        List<ReportColumn> columns = reportColumnRepository
                .findByRptSrcIdOrderByRptColOrder(reportSource.getRptSrcId());
        
        String sqlQuery = buildSqlQuery(reportSource, columns, parameters);
        log.info("Executing query: {}", sqlQuery);
        
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
        
        ReportData reportData = new ReportData();
        reportData.setReportName(reportSource.getRptName());
        reportData.setDescription(reportSource.getDescription());
        reportData.setColumns(columns.stream()
                .map(col -> col.getRptColAlias() != null ? col.getRptColAlias() : col.getRptColName())
                .toList());
        reportData.setRows(rows);
        reportData.setSqlQuery(sqlQuery);
        reportData.setParameters(parameters);
        
        return reportData;
    }
    
    private String buildSqlQuery(ReportSource reportSource, List<ReportColumn> columns, 
                                  Map<String, Object> parameters) {
        StringBuilder query = new StringBuilder("SELECT ");
        
        for (int i = 0; i < columns.size(); i++) {
            ReportColumn col = columns.get(i);
            query.append(col.getRptColName());
            if (col.getRptColAlias() != null) {
                query.append(" AS ").append(col.getRptColAlias());
            }
            if (i < columns.size() - 1) {
                query.append(", ");
            }
        }
        
        query.append(" FROM (").append(reportSource.getBaseQuery()).append(") base_data");
        
        if (parameters != null && !parameters.isEmpty()) {
            query.append(" WHERE 1=1");
            parameters.forEach((key, value) -> {
                if (value != null && !value.toString().isEmpty()) {
                    query.append(" AND ").append(key).append(" = '").append(value).append("'");
                }
            });
        }
        
        return query.toString();
    }
    
    public List<ReportParameter> getReportParameters(String reportName) {
        ReportSource reportSource = reportSourceRepository.findByRptName(reportName)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportName));
        return reportParameterRepository.findByRptSrcId(reportSource.getRptSrcId());
    }
}
