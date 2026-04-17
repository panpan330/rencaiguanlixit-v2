package com.rehab.managerv2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_talent_education")
public class BizTalentEducation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 人才档案ID */
    private Long talentId;
    
    /** 学校名称 */
    private String schoolName;
    
    /** 学位(如：学士、硕士、博士) */
    private String degree;
    
    /** 专业名称 */
    private String major;
    
    /** 入学日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    /** 毕业日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
}
