package com.rehab.managerv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.BizTalentExperience;
import com.rehab.managerv2.service.BizTalentExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biz-talent-experience")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BizTalentExperienceController {

    @Autowired
    private BizTalentExperienceService experienceService;

    @GetMapping("/list/{talentId}")
    public Result<List<BizTalentExperience>> getListByTalentId(@PathVariable Long talentId) {
        LambdaQueryWrapper<BizTalentExperience> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizTalentExperience::getTalentId, talentId).orderByDesc(BizTalentExperience::getStartDate);
        return Result.success(experienceService.list(wrapper));
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody BizTalentExperience experience) {
        return experienceService.saveOrUpdate(experience) ? Result.success("保存成功") : Result.error(500, "保存失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return experienceService.removeById(id) ? Result.success("删除成功") : Result.error(500, "删除失败");
    }
}
