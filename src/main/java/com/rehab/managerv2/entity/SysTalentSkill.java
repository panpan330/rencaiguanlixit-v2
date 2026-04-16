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
 * 交叉人才技能雷达分数表
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_talent_skill")
public class SysTalentSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的人才档案ID
     */
    @TableField("talent_id")
    private Long talentId;

    /**
     * 编程开发(Dev)
     */
    @TableField("dev_score")
    private Integer devScore;

    /**
     * 算法设计(Algo)
     */
    @TableField("algo_score")
    private Integer algoScore;

    /**
     * 临床评估(Clinical)
     */
    @TableField("clinical_score")
    private Integer clinicalScore;

    /**
     * 生理基础(Physio)
     */
    @TableField("physio_score")
    private Integer physioScore;

    /**
     * 硬件交互(Hardware)
     */
    @TableField("hardware_score")
    private Integer hardwareScore;

    /**
     * 核心优势总结
     */
    @TableField("strong_points")
    private String strongPoints;


}
