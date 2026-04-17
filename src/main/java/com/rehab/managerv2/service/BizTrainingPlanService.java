package com.rehab.managerv2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rehab.managerv2.entity.BizTrainingPlan;

import java.util.List;

public interface BizTrainingPlanService extends IService<BizTrainingPlan> {

    /**
     * 基于人才档案进行智能培训计划推荐
     * @param talentId 人才ID
     * @return 推荐的培训计划列表
     */
    List<BizTrainingPlan> recommendPlans(Long talentId);
}
