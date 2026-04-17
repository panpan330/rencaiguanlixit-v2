package com.rehab.managerv2.entity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.ExcelIgnore;
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
 * 交叉人才基本档案表
 * </p>
 *
 * @author author
 * @since 2026-04-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_talent_profile")
public class SysTalentProfile implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @ExcelProperty("档案编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联sys_user表的账号ID(暂留空)
     */
    @ExcelIgnore
    @TableField("user_id")
    private Long userId;

    /**
     * 人才真实姓名
     */
    @ExcelProperty("真实姓名")
    @TableField("real_name")
    private String realName;

    /**
     * 联系电话
     */
    @ExcelProperty("联系电话")
    @TableField("phone")
    private String phone;

    /**
     * 主攻领域：0-计算机科学, 1-康复医学, 2-深度交叉
     */
    @ExcelProperty("主攻领域(0计科,1康复,2交叉)")
    @TableField("primary_domain")
    private Integer primaryDomain;

    /**
     * 最高学历(如：本科, 硕士, 博士)
     */
    @ExcelProperty("最高学历")
    @TableField("education_level")
    private String educationLevel;

    /**
     * 主要研究方向(如：脑机接口、运动数据分析)
     */
    @ExcelProperty("研究方向")
    @TableField("research_direction")
    private String researchDirection;

    /**
     * 就业状态：0-在校培养, 1-科研院所, 2-企业就职
     */
    @ExcelProperty("就业状态代码")
    @TableField("employment_status")
    private Integer employmentStatus;

    /**
     * 建档时间
     */
    @ExcelProperty("建档时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelIgnore
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @ExcelIgnore
    @TableField("is_deleted")
    private Integer isDeleted;


}
