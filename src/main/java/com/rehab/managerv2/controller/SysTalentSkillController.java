package com.rehab.managerv2.controller;


import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysTalentSkill;
import com.rehab.managerv2.service.SysTalentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        SysTalentSkill skill = sysTalentSkillService.getSkillByTalentId(talentId);

        return skill != null ?
                Result.success(skill) :
                Result.error(404, "该人才尚未录入技能分数");
    }

    @GetMapping("/ai-evaluate/{talentId}")
    public Result<SysTalentSkill> aiEvaluate(@PathVariable Long talentId) {
        SysTalentSkill skill = sysTalentSkillService.aiEvaluateSkill(talentId);
        return skill != null ? Result.success(skill) : Result.error(500, "智能评估失败");
    }

    @PostMapping("/save")
    public Result<?> saveSkill(@RequestBody SysTalentSkill skill) {
        // 先检查是否已经有该人才的技能记录
        SysTalentSkill existing = sysTalentSkillService.getSkillByTalentId(skill.getTalentId());
        if (existing != null) {
            skill.setId(existing.getId()); // 更新
        }
        
        // 自动生成强项分析
        skill.setStrongPoints(generateStrongPoints(skill));
        
        try {
            boolean success = sysTalentSkillService.saveOrUpdate(skill);
            return success ? Result.success("技能分数保存成功") : Result.error(500, "保存失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "后端保存异常: " + e.getMessage());
        }
    }

    private String generateStrongPoints(SysTalentSkill skill) {
        StringBuilder sb = new StringBuilder();
        if (skill.getDevScore() != null && skill.getDevScore() >= 80) sb.append("编程开发、");
        if (skill.getAlgoScore() != null && skill.getAlgoScore() >= 80) sb.append("算法设计、");
        if (skill.getClinicalScore() != null && skill.getClinicalScore() >= 80) sb.append("临床评估、");
        if (skill.getPhysioScore() != null && skill.getPhysioScore() >= 80) sb.append("生理基础、");
        if (skill.getHardwareScore() != null && skill.getHardwareScore() >= 80) sb.append("硬件交互、");
        
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1) + "能力突出";
        } else {
            // 找出最高分
            int max = 0;
            String topSkill = "各项";
            if (skill.getDevScore() != null && skill.getDevScore() > max) { max = skill.getDevScore(); topSkill = "编程开发"; }
            if (skill.getAlgoScore() != null && skill.getAlgoScore() > max) { max = skill.getAlgoScore(); topSkill = "算法设计"; }
            if (skill.getClinicalScore() != null && skill.getClinicalScore() > max) { max = skill.getClinicalScore(); topSkill = "临床评估"; }
            if (skill.getPhysioScore() != null && skill.getPhysioScore() > max) { max = skill.getPhysioScore(); topSkill = "生理基础"; }
            if (skill.getHardwareScore() != null && skill.getHardwareScore() > max) { max = skill.getHardwareScore(); topSkill = "硬件交互"; }
            
            if (max > 60) {
                return topSkill + "方向具有一定潜力";
            }
            return "各方面能力较为基础，有待进一步提升";
        }
    }

}
