package com.rehab.managerv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.BizTalentAchievement;
import com.rehab.managerv2.service.BizTalentAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biz-talent-achievement")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BizTalentAchievementController {

    @Autowired
    private BizTalentAchievementService achievementService;

    @GetMapping("/list/{talentId}")
    public Result<List<BizTalentAchievement>> getListByTalentId(@PathVariable Long talentId) {
        LambdaQueryWrapper<BizTalentAchievement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizTalentAchievement::getTalentId, talentId).orderByDesc(BizTalentAchievement::getPublishDate);
        return Result.success(achievementService.list(wrapper));
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody BizTalentAchievement achievement) {
        return achievementService.saveOrUpdate(achievement) ? Result.success("保存成功") : Result.error(500, "保存失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return achievementService.removeById(id) ? Result.success("删除成功") : Result.error(500, "删除失败");
    }
}
