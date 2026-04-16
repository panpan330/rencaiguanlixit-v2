package com.rehab.managerv2.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 项目能力需求指标表
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_project_requirement")
public class BizProjectRequirement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联biz_project表ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 编程开发要求门槛(0-100)
     */
    @TableField("dev_req")
    private Integer devReq;

    /**
     * 算法设计要求门槛(0-100)
     */
    @TableField("algo_req")
    private Integer algoReq;

    /**
     * 临床评估要求门槛(0-100)
     */
    @TableField("clinical_req")
    private Integer clinicalReq;

    /**
     * 生理基础要求门槛(0-100)
     */
    @TableField("physio_req")
    private Integer physioReq;

    /**
     * 硬件交互要求门槛(0-100)
     */
    @TableField("hardware_req")
    private Integer hardwareReq;

    /**
     * 具体需求文字描述
     */
    @TableField("description")
    private String description;


}
