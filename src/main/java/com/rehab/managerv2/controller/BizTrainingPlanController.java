package com.rehab.managerv2.controller;

import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.BizTrainingPlan;
import com.rehab.managerv2.service.BizTrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能培训推荐接口
 */
@RestController
@RequestMapping("/api/training")
public class BizTrainingPlanController {

    @Autowired
    private BizTrainingPlanService trainingPlanService;

    /**
     * 根据人才画像智能推荐培训计划
     */
    @GetMapping("/recommend/{talentId}")
    public Result<List<BizTrainingPlan>> recommend(@PathVariable Long talentId) {
        List<BizTrainingPlan> list = trainingPlanService.recommendPlans(talentId);
        return Result.success(list);
    }
}
