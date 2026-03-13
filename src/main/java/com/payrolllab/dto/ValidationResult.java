package com.payrolllab.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ValidationResult {
    private String checkName;
    private BigDecimal expectedValue;
    private BigDecimal actualValue;
    private boolean passed;
    private String message;
}
