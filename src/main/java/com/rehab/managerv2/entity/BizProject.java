package com.rehab.managerv2.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 科研/企业项目表
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_project")
public class BizProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联sys_user表的账号ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 项目名称(如：智能外骨骼研发)
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 发布方(如：某某医院/某某高校)
     */
    @TableField("publisher")
    private String publisher;

    /**
     * 所属领域：0-计算机科学, 1-康复医学, 2-深度交叉
     */
    @TableField("domain")
    private Integer domain;

    /**
     * 项目预算(万元)
     */
    @TableField("budget")
    private BigDecimal budget;

    /**
     * 状态：0-招募中, 1-进行中, 2-已结题
     */
    @TableField("status")
    private Integer status;

    /**
     * 发布时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;


}
