package com.rehab.managerv2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 智能培训计划表
 */
@Data
@TableName("biz_training_plan")
public class BizTrainingPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String planName;

    // 目标领域：0-计算机科学, 1-康复医学, 2-深度交叉
    private Integer targetDomain;

    // 难度等级：1-初级, 2-中级, 3-高级
    private Integer difficultyLevel;

    private String contentDesc;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
