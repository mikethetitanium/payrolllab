package com.payrolllab.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "deductions")
@Data
public class Deduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(name = "deduction_type", nullable = false)
    private String deductionType;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
}
