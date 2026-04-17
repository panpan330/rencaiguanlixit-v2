package com.rehab.managerv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.BizTalentEducation;
import com.rehab.managerv2.service.BizTalentEducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biz-talent-education")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BizTalentEducationController {

    @Autowired
    private BizTalentEducationService educationService;

    @GetMapping("/list/{talentId}")
    public Result<List<BizTalentEducation>> getListByTalentId(@PathVariable Long talentId) {
        LambdaQueryWrapper<BizTalentEducation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizTalentEducation::getTalentId, talentId).orderByDesc(BizTalentEducation::getStartDate);
        return Result.success(educationService.list(wrapper));
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody BizTalentEducation education) {
        return educationService.saveOrUpdate(education) ? Result.success("保存成功") : Result.error(500, "保存失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return educationService.removeById(id) ? Result.success("删除成功") : Result.error(500, "删除失败");
    }
}
