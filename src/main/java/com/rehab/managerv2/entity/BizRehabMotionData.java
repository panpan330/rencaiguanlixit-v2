package com.rehab.managerv2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 康复运动时空数据归档表
 */
@Data
@TableName("biz_rehab_motion_data")
public class BizRehabMotionData {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long talentId;
    
    private String deviceId;
    
    private String motionType;
    
    private BigDecimal angleX;
    
    private BigDecimal angleY;
    
    private BigDecimal angleZ;
    
    private BigDecimal forceValue;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date recordTime;
    
    // 这里简化处理空间数据，MyBatis-Plus 对 Point 类型的支持比较特殊，这里暂存为 String 或交由后端特定解析
    // 若仅为演示可暂不处理 location_point
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
