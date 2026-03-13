package com.payrolllab.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "report_columns")
@Data
public class ReportColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "rpt_src_id", nullable = false)
    private Long rptSrcId;
    
    @Column(name = "rpt_col_name", nullable = false)
    private String rptColName;
    
    @Column(name = "rpt_col_alias")
    private String rptColAlias;
    
    @Column(name = "rpt_col_order")
    private Integer rptColOrder;
    
    @Column(name = "rpt_col_data_type")
    private String rptColDataType;
}
