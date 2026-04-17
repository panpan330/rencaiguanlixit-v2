package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rehab.managerv2.entity.BizTrainingPlan;
import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.entity.SysTalentSkill;
import com.rehab.managerv2.mapper.BizTrainingPlanMapper;
import com.rehab.managerv2.service.BizTrainingPlanService;
import com.rehab.managerv2.service.SysTalentProfileService;
import com.rehab.managerv2.service.SysTalentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BizTrainingPlanServiceImpl extends ServiceImpl<BizTrainingPlanMapper, BizTrainingPlan> implements BizTrainingPlanService {

    @Autowired
    private SysTalentProfileService profileService;

    @Autowired
    private SysTalentSkillService skillService;

    @Override
    public List<BizTrainingPlan> recommendPlans(Long talentId) {
        // 1. 获取人才基本档案
        SysTalentProfile profile = profileService.getById(talentId);
        if (profile == null) {
            return new ArrayList<>();
        }

        // 2. 获取技能画像 (若无技能画像则返回基础通识课程)
        SysTalentSkill skill = skillService.lambdaQuery().eq(SysTalentSkill::getTalentId, talentId).one();
        
        Integer targetDomain = profile.getPrimaryDomain();
        Integer difficulty = 1; // 默认初级

        // 简单智能决策：如果已有较高技能分数，则推荐高级课程，否则推荐初级
        if (skill != null) {
            // 综合分数评估
            int totalScore = (skill.getDevScore() != null ? skill.getDevScore() : 0) +
                             (skill.getAlgoScore() != null ? skill.getAlgoScore() : 0) +
                             (skill.getClinicalScore() != null ? skill.getClinicalScore() : 0) +
                             (skill.getPhysioScore() != null ? skill.getPhysioScore() : 0) +
                             (skill.getHardwareScore() != null ? skill.getHardwareScore() : 0);
            int avgScore = totalScore / 5;

            if (avgScore >= 80) {
                difficulty = 3; // 高级
            } else if (avgScore >= 60) {
                difficulty = 2; // 中级
            } else {
                difficulty = 1; // 初级
            }
        }

        // 3. 根据目标领域和难度从数据库匹配
        // 为了确保有数据，除了匹配当前领域，也可以加上深度交叉(2)的课程
        return this.lambdaQuery()
                .eq(BizTrainingPlan::getStatus, 0)
                .in(BizTrainingPlan::getTargetDomain, targetDomain, 2)
                .le(BizTrainingPlan::getDifficultyLevel, difficulty + 1) // 允许向上挑战一级
                .orderByDesc(BizTrainingPlan::getDifficultyLevel)
                .last("limit 3")
                .list();
    }
}
