package com.payrolllab.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "payroll_runs")
@Data
public class PayrollRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payroll_month", nullable = false)
    private String payrollMonth;
    
    @Column(nullable = false)
    private String country;
    
    @Column(name = "run_date", nullable = false)
    private LocalDate runDate;
}
