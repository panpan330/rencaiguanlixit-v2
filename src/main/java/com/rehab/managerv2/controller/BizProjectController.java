package com.rehab.managerv2.controller;


import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.BizProject;
import com.rehab.managerv2.service.BizProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 科研/企业项目表 前端控制器
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@RestController
@RequestMapping("/biz-project")
public class BizProjectController {

    @Autowired
    private BizProjectService projectService;

    @GetMapping("/list")
    public Result getList() {
        // 查出所有项目
        return Result.success(projectService.list());
    }

    @PostMapping("/save")
    public Result save(@RequestBody BizProject project) {
        // 新增或修改项目
        return projectService.saveOrUpdate(project) ?
                Result.success("保存成功") : Result.error(500, "保存失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return projectService.removeById(id) ?
                Result.success("删除成功") : Result.error(500, "删除失败");
    }

    /**
     * 触发项目智能匹配
     */
    @PostMapping("/smart-match/{projectId}")
    public Result<String> triggerSmartMatch(@PathVariable Long projectId) {
        try {
            int count = projectService.executeSmartMatch(projectId);
            return Result.success("智能撮合成功！已为该项目匹配到 " + count + " 位杰出人才，并已自动发送系统站内信！");
        } catch (Exception e) {
            return Result.error(500, "匹配失败：" + e.getMessage());
        }

    }
}
