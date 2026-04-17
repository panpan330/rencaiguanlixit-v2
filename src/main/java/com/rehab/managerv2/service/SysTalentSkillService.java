package com.rehab.managerv2.service;

import com.rehab.managerv2.entity.SysTalentSkill;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 交叉人才技能雷达分数表 服务类
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
public interface SysTalentSkillService extends IService<SysTalentSkill> {
    /**
     * 根据人才 ID 查询真实的五维技能分数
     */
    SysTalentSkill getSkillByTalentId(Long talentId);

    /**
     * 智能一键评估人才技能
     */
    SysTalentSkill aiEvaluateSkill(Long talentId);

}
