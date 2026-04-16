package com.rehab.managerv2.entity;

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
 * 智能撮合历史记录表
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_match_record")
public class BizMatchRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 匹配到的人才ID
     */
    @TableField("talent_id")
    private Long talentId;

    /**
     * 综合匹配度得分(如：92)
     */
    @TableField("match_score")
    private Integer matchScore;

    /**
     * 处理状态：0-已推荐, 1-已发送邀请, 2-已拒绝, 3-已合作
     */
    @TableField("status")
    private Integer status;

    /**
     * 撮合计算时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;


}
