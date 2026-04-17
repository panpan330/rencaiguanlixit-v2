package com.rehab.managerv2.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class MatchReportExportDTO {
    
    @ExcelProperty("排名")
    @ColumnWidth(10)
    private Integer rank;

    @ExcelProperty("候选人姓名")
    @ColumnWidth(20)
    private String talentName;

    @ExcelProperty("联系电话")
    @ColumnWidth(20)
    private String phone;

    @ExcelProperty("最高学历")
    @ColumnWidth(15)
    private String educationLevel;

    @ExcelProperty("AI综合匹配度 (%)")
    @ColumnWidth(20)
    private String matchScore;

    @ExcelProperty("编程开发分")
    @ColumnWidth(15)
    private Integer devScore;

    @ExcelProperty("算法设计分")
    @ColumnWidth(15)
    private Integer algoScore;

    @ExcelProperty("临床评估分")
    @ColumnWidth(15)
    private Integer clinicalScore;

    @ExcelProperty("生理基础分")
    @ColumnWidth(15)
    private Integer physioScore;

    @ExcelProperty("硬件交互分")
    @ColumnWidth(15)
    private Integer hardwareScore;

    @ExcelProperty("核心优势分析")
    @ColumnWidth(50)
    private String strongPoints;
}