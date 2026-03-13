package com.payrolllab.service;

import com.opencsv.CSVWriter;
import com.payrolllab.dto.ReportData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExportService {
    
    public byte[] exportToExcel(ReportData reportData) {
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(reportData.getReportName());
            
            // Header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            List<String> columns = reportData.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            // Data rows
            int rowNum = 1;
            for (Map<String, Object> row : reportData.getRows()) {
                Row dataRow = sheet.createRow(rowNum++);
                for (int i = 0; i < columns.size(); i++) {
                    Cell cell = dataRow.createCell(i);
                    Object value = row.get(columns.get(i).toLowerCase());
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }
            
            // Auto-size columns
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
            
        } catch (Exception e) {
            log.error("Error exporting to Excel", e);
            throw new RuntimeException("Failed to export to Excel", e);
        }
    }
    
    public String exportToCsv(ReportData reportData) {
        try (StringWriter sw = new StringWriter();
             CSVWriter writer = new CSVWriter(sw)) {
            
            // Header
            List<String> columns = reportData.getColumns();
            writer.writeNext(columns.toArray(new String[0]));
            
            // Data rows
            for (Map<String, Object> row : reportData.getRows()) {
                String[] values = new String[columns.size()];
                for (int i = 0; i < columns.size(); i++) {
                    Object value = row.get(columns.get(i).toLowerCase());
                    values[i] = value != null ? value.toString() : "";
                }
                writer.writeNext(values);
            }
            
            return sw.toString();
            
        } catch (Exception e) {
            log.error("Error exporting to CSV", e);
            throw new RuntimeException("Failed to export to CSV", e);
        }
    }
}
