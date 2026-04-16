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
 * 字典数据表
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_dict_data")
public class SysDictData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    @TableId(value = "dict_code", type = IdType.AUTO)
    private Long dictCode;

    /**
     * 字典排序
     */
    @TableField("dict_sort")
    private Integer dictSort;

    /**
     * 字典标签(如：博士)
     */
    @TableField("dict_label")
    private String dictLabel;

    /**
     * 字典键值(如：doctor)
     */
    @TableField("dict_value")
    private String dictValue;

    /**
     * 字典类型(关联sys_dict_type)
     */
    @TableField("dict_type")
    private String dictType;


}
