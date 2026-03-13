package com.payrolllab.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "report_parameters")
@Data
public class ReportParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "rpt_src_id", nullable = false)
    private Long rptSrcId;
    
    @Column(name = "parameter_name", nullable = false)
    private String parameterName;
    
    @Column(name = "parameter_type", nullable = false)
    private String parameterType;
    
    @Column(name = "default_value")
    private String defaultValue;
}
