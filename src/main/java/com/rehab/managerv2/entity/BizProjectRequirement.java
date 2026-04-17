package com.rehab.managerv2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("biz_project_requirement")
public class BizProjectRequirement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 关联biz_project表ID */
    private Long projectId;
    
    /** 编程开发要求门槛(0-100) */
    private Integer devReq;
    
    /** 算法设计要求门槛(0-100) */
    private Integer algoReq;
    
    /** 临床评估要求门槛(0-100) */
    private Integer clinicalReq;
    
    /** 生理基础要求门槛(0-100) */
    private Integer physioReq;
    
    /** 硬件交互要求门槛(0-100) */
    private Integer hardwareReq;
    
    /** 具体需求文字描述 */
    private String description;
}
