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
 * 科研成果表
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_talent_achievement")
public class BizTalentAchievement implements Serializable {

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
     * 成果/论文/专利名称
     */
    @TableField("title")
    private String title;

    /**
     * 类型：0-SCI论文, 1-发明专利, 2-软著, 3-其他
     */
    @TableField("type")
    private Integer type;

    /**
     * 发表/授权日期
     */
    @TableField("publish_date")
    private LocalDate publishDate;

    /**
     * 证明材料链接(备用)
     */
    @TableField("url")
    private String url;


}
