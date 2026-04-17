package com.rehab.managerv2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_talent_achievement")
public class BizTalentAchievement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 人才档案ID */
    private Long talentId;
    
    /** 成果类型(0:SCI论文, 1:发明专利, 2:软著, 3:其他) */
    private Integer type;
    
    /** 成果名称/标题 */
    private String title;
    
    /** 获得/发表日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date publishDate;
    
    /** 成果链接或证明文件URL */
    private String url;
}
