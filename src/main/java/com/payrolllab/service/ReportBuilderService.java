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
        String baseQuery = reportSource.getBaseQuery();
        boolean hasGroupBy = baseQuery.toUpperCase().contains("GROUP BY");
        
        // For aggregated queries, wrap in subquery to apply column aliases
        if (hasGroupBy) {
            String modifiedQuery = injectWhereBeforeGroupBy(baseQuery, parameters);
            
            // Wrap aggregated query in subquery to apply column aliases
            StringBuilder query = new StringBuilder("SELECT ");
            
            if (columns == null || columns.isEmpty()) {
                query.append("*");
            } else {
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
            }
            
            query.append(" FROM (").append(modifiedQuery).append(") base_data");
            return query.toString();
        }
        
        // For simple queries, wrap in subquery and add WHERE after
        StringBuilder query = new StringBuilder("SELECT ");
        
        if (columns == null || columns.isEmpty()) {
            query.append("*");
        } else {
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
        }
        
        query.append(" FROM (").append(baseQuery).append(") base_data");
        
        if (parameters != null && !parameters.isEmpty()) {
            query.append(" WHERE 1=1");
            parameters.forEach((key, value) -> {
                if (value != null && !value.toString().isEmpty()) {
                    String columnName = mapParameterToColumn(key);
                    query.append(" AND base_data.").append(columnName).append(" = '").append(value).append("'");
                }
            });
        }
        
        return query.toString();
    }
    
    private String injectWhereBeforeGroupBy(String baseQuery, Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return baseQuery;
        }
        
        String upperQuery = baseQuery.toUpperCase();
        int groupByIndex = upperQuery.indexOf("GROUP BY");
        
        if (groupByIndex == -1) {
            // No GROUP BY found, just add WHERE at the end
            return baseQuery + " WHERE 1=1" + buildWhereConditions(parameters);
        }
        
        // Build WHERE clause
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
        parameters.forEach((key, value) -> {
            if (value != null && !value.toString().isEmpty()) {
                String columnName = mapParameterToColumn(key);
                whereClause.append(" AND ").append(columnName).append(" = '").append(value).append("'");
            }
        });
        
        // Insert WHERE before GROUP BY
        String beforeGroupBy = baseQuery.substring(0, groupByIndex).trim();
        String groupByPart = baseQuery.substring(groupByIndex);
        
        return beforeGroupBy + whereClause + " " + groupByPart;
    }
    
    private String buildWhereConditions(Map<String, Object> parameters) {
        StringBuilder conditions = new StringBuilder();
        parameters.forEach((key, value) -> {
            if (value != null && !value.toString().isEmpty()) {
                String columnName = mapParameterToColumn(key);
                conditions.append(" AND ").append(columnName).append(" = '").append(value).append("'");
            }
        });
        return conditions.toString();
    }
    
    private String mapParameterToColumn(String parameterName) {
        // Map URL parameter names to database column names
        return switch (parameterName) {
            case "country" -> "country";
            case "payrollMonth" -> "payroll_month";
            case "payroll_month" -> "payroll_month";
            case "department" -> "department";
            default -> parameterName;
        };
    }
    
    public List<ReportParameter> getReportParameters(String reportName) {
        ReportSource reportSource = reportSourceRepository.findByRptName(reportName)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportName));
        return reportParameterRepository.findByRptSrcId(reportSource.getRptSrcId());
    }
}
