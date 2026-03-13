package com.payrolllab.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "report_sources")
@Data
public class ReportSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rpt_src_id")
    private Long rptSrcId;
    
    @Column(name = "rpt_name", nullable = false, unique = true)
    private String rptName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "base_query", columnDefinition = "TEXT")
    private String baseQuery;
}
