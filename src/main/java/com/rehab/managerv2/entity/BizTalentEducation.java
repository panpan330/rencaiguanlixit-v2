package com.rehab.managerv2.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 人才教育经历表
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_talent_education")
public class BizTalentEducation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 人才档案ID
     */
    @TableField("talent_id")
    private Long talentId;

    /**
     * 学校名称
     */
    @TableField("school_name")
    private String schoolName;

    /**
     * 学位(如：学士、硕士、博士)
     */
    @TableField("degree")
    private String degree;

    /**
     * 专业名称
     */
    @TableField("major")
    private String major;

    /**
     * 入学日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 毕业日期
     */
    @TableField("end_date")
    private LocalDate endDate;


}
