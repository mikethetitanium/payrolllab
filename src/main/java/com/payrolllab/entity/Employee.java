package com.payrolllab.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_name", nullable = false)
    private String employeeName;
    
    @Column(nullable = false)
    private String country;
    
    @Column(nullable = false)
    private String department;
    
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;
}
