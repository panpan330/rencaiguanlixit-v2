package com.rehab.managerv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.BizProjectRequirement;
import com.rehab.managerv2.service.BizProjectRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/biz-project-requirement")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BizProjectRequirementController {

    @Autowired
    private BizProjectRequirementService requirementService;

    @GetMapping("/{projectId}")
    public Result<BizProjectRequirement> getByProjectId(@PathVariable Long projectId) {
        LambdaQueryWrapper<BizProjectRequirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizProjectRequirement::getProjectId, projectId);
        return Result.success(requirementService.getOne(wrapper));
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody BizProjectRequirement requirement) {
        // 每个项目只有一条需求记录，先查询是否存在
        LambdaQueryWrapper<BizProjectRequirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizProjectRequirement::getProjectId, requirement.getProjectId());
        BizProjectRequirement existing = requirementService.getOne(wrapper);
        
        if (existing != null) {
            requirement.setId(existing.getId());
        }
        
        return requirementService.saveOrUpdate(requirement) ? Result.success("项目需求门槛设置成功") : Result.error(500, "设置失败");
    }
}
