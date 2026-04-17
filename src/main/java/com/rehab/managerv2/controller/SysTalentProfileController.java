package com.rehab.managerv2.controller;

import com.rehab.managerv2.annotation.Log;
import com.rehab.managerv2.entity.SysTalentProfile;
// 注意：这里最好统一使用 ISysTalentProfileService 接口
import com.rehab.managerv2.service.SysTalentProfileService;
import com.rehab.managerv2.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys-talent-profile")
public class SysTalentProfileController {

    @Autowired
    private SysTalentProfileService sysTalentProfileService;

    @Autowired
    private com.rehab.managerv2.service.SysTalentSkillService sysTalentSkillService;

    @GetMapping("/list")
    public Result getList(@RequestParam(required = false) Long expertId,
                          @RequestParam(required = false) String role,
                          @RequestParam(required = false) String realName,
                          @RequestParam(required = false) Integer primaryDomain) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysTalentProfile> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        // 核心逻辑：如果当前登录角色是教研专家，则只能查看属于他自己的学生档案
        if ("expert".equals(role) && expertId != null) {
            wrapper.eq(SysTalentProfile::getUserId, expertId);
        }

        // 处理查询参数
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(SysTalentProfile::getRealName, realName);
        }
        if (primaryDomain != null) {
            wrapper.eq(SysTalentProfile::getPrimaryDomain, primaryDomain);
        }
        
        // 其他情况（如超级管理员），不做过滤，返回所有档案
        return Result.success(sysTalentProfileService.list(wrapper));
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        SysTalentProfile profile = sysTalentProfileService.getById(id);
        return profile != null ? Result.success(profile) : Result.error(404, "未找到该档案");
    }

    @PostMapping("/save")
    public Result save(@RequestBody SysTalentProfile sysTalentProfile) {
        boolean isNew = (sysTalentProfile.getId() == null);
        boolean success = sysTalentProfileService.saveOrUpdate(sysTalentProfile);
        
        if (success && isNew) {
            // 当新增人才时，自动在数据库中生成默认的技能打分记录
            com.rehab.managerv2.entity.SysTalentSkill defaultSkill = new com.rehab.managerv2.entity.SysTalentSkill();
            defaultSkill.setTalentId(sysTalentProfile.getId());
            defaultSkill.setDevScore(50);
            defaultSkill.setAlgoScore(50);
            defaultSkill.setClinicalScore(50);
            defaultSkill.setPhysioScore(50);
            defaultSkill.setHardwareScore(50);
            defaultSkill.setStrongPoints("各方面能力较为基础，有待进一步提升");
            sysTalentSkillService.save(defaultSkill);

            // 自动为学生创建登录账号 (账号为手机号，密码默认为123456，角色为学生: 3)
            if (sysTalentProfile.getPhone() != null && !sysTalentProfile.getPhone().isEmpty()) {
                com.rehab.managerv2.entity.SysUser studentUser = new com.rehab.managerv2.entity.SysUser();
                studentUser.setUsername(sysTalentProfile.getPhone());
                studentUser.setRealName(sysTalentProfile.getRealName());
                studentUser.setPhone(sysTalentProfile.getPhone());
                studentUser.setRoleType(3); // 3-学生
                // 为了避免依赖注入循环，我们可以直接利用 Spring 容器获取 SysUserService
                com.rehab.managerv2.service.SysUserService sysUserService = 
                    com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.SysUserService.class);
                if (sysUserService != null) {
                    try {
                        sysUserService.saveUserWithEncryption(studentUser);
                    } catch(Exception e) {
                        // 忽略账号已存在的异常
                    }
                }
            }
        }
        
        return success ? Result.success("保存成功") : Result.error(500, "保存失败");
    }

    @Log("删除人才档案")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return sysTalentProfileService.removeById(id) ?
                Result.success("删除成功") : Result.error(500, "删除失败");
    }

    /**
     * 🔥 重构后的导出接口：彻底解耦
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 1. 服务员 (Controller) 只负责他该干的事：处理 HTTP 协议和响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("交叉人才档案数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 2. 把输出流 (OutputStream) 扔给大厨 (Service)，让他去把数据写进流里！
        sysTalentProfileService.exportDataToStream(response.getOutputStream());
    }

    @PostMapping("/smart-match")
    public Result smartMatch(@RequestBody Map<String, Integer> weights) {
        return Result.success(sysTalentProfileService.calculateSmartMatch(weights));
    }
}