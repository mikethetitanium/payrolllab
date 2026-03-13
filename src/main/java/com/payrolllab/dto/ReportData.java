package com.payrolllab.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ReportData {
    private String reportName;
    private String description;
    private List<String> columns;
    private List<Map<String, Object>> rows;
    private String sqlQuery;
    private Map<String, Object> parameters;
}
