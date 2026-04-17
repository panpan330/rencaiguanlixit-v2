package com.rehab.managerv2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_talent_experience")
public class BizTalentExperience {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 人才档案ID */
    private Long talentId;
    
    /** 单位/课题组名称 */
    private String organization;
    
    /** 担任职务/角色 */
    private String position;
    
    /** 工作内容详述 */
    private String description;
    
    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
}
