package com.rehab.managerv2.service;

import com.rehab.managerv2.entity.SysTalentProfile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 交叉人才基本档案表 服务类
 * </p>
 *
 * @author author
 * @since 2026-04-15
 */
public interface SysTalentProfileService extends IService<SysTalentProfile> {
    /**
     * 🔥 规范化：将复杂的智能撮合逻辑定义在 Service 接口中
     * @param weights 权重矩阵
     * @return 计算后的 Top 3 推荐列表
     */
    List<Map<String, Object>> calculateSmartMatch(Map<String, Integer> weights);

}
