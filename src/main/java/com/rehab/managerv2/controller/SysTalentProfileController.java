package com.rehab.managerv2.controller;

import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.service.SysTalentProfileService;
import com.rehab.managerv2.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys-talent-profile")
public class SysTalentProfileController {

    @Autowired
    private SysTalentProfileService sysTalentProfileService;

    /**
     * 获取所有人才档案列表
     */
    @GetMapping("/list")
    public Result getList() {
        List<SysTalentProfile> list = sysTalentProfileService.list();
        return Result.success(list);
    }

    /**
     * 保存或更新人才档案
     */
    @PostMapping("/save")
    public Result save(@RequestBody SysTalentProfile sysTalentProfile) {
        boolean success = sysTalentProfileService.saveOrUpdate(sysTalentProfile);
        if (success) {
            return Result.success("保存成功");
        }
        // 🔥 修改点：加上了 500 错误码，完美匹配你的 Result 类！
        return Result.error(500, "保存失败");
    }

    /**
     * 删除人才档案
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = sysTalentProfileService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        // 🔥 修改点：加上了 500 错误码！
        return Result.error(500, "删除失败");
    }
}