package com.rehab.managerv2.controller;


import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysTalentSkill;
import com.rehab.managerv2.service.SysTalentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 交叉人才技能雷达分数表 前端控制器
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@RestController
@RequestMapping("/sys-talent-skill")
public class SysTalentSkillController {

    @Autowired
    private SysTalentSkillService sysTalentSkillService;

    @GetMapping("/talent/{talentId}")
    public Result<SysTalentSkill> getSkillByTalentId(@PathVariable Long talentId) {
        // 服务员只负责调用大厨，并根据结果返回给前端不同的提示
        SysTalentSkill skill = sysTalentSkillService.getSkillByTalentId(talentId);

        return skill != null ?
                Result.success(skill) :
                Result.error(404, "该人才尚未录入技能分数");
    }

}
