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
    public Result getList(@RequestParam(required = false) Long projectId,
                          @RequestParam(required = false) String role,
                          @RequestParam(required = false) Long userId,
                          @RequestParam(required = false) Long talentId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BizProject> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            
        // 如果是项目方，只能看自己创建的项目
        if ("project".equals(role) && userId != null) {
            wrapper.eq(BizProject::getUserId, userId);
        }
        
        // 如果是学生端(talentId存在)，我们需要去查 biz_match_record 中他已接受 (status=3) 的项目
        if ("student".equals(role) && talentId != null) {
            com.rehab.managerv2.service.BizMatchRecordService matchService = 
                com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.BizMatchRecordService.class);
            java.util.List<com.rehab.managerv2.entity.BizMatchRecord> records = matchService.lambdaQuery()
                    .eq(com.rehab.managerv2.entity.BizMatchRecord::getTalentId, talentId)
                    .eq(com.rehab.managerv2.entity.BizMatchRecord::getStatus, 3)
                    .list();
            if (records.isEmpty()) {
                return Result.success(new java.util.ArrayList<>()); // 没有参与的项目
            }
            java.util.List<Long> projectIds = records.stream().map(com.rehab.managerv2.entity.BizMatchRecord::getProjectId).collect(java.util.stream.Collectors.toList());
            wrapper.in(BizProject::getId, projectIds);
        }
        
        return Result.success(projectService.list(wrapper));
    }

    @PostMapping("/save")
    public Result save(@RequestBody BizProject project) {
        // 新增或修改项目
        return projectService.saveOrUpdate(project) ?
                Result.success("保存成功") : Result.error(500, "保存失败");
    }

    @PostMapping("/update-status/{id}")
    public Result updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        BizProject project = new BizProject();
        project.setId(id);
        project.setStatus(status);
        return projectService.updateById(project) ?
                Result.success("状态更新成功") : Result.error(500, "状态更新失败");
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
