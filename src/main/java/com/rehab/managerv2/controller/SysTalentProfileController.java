package com.rehab.managerv2.controller;


import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.service.SysTalentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 交叉人才基本档案表 前端控制器
 * </p>
 *
 * @author author
 * @since 2026-04-15
 */
@RestController
@RequestMapping("/sys-talent-profile")
public class SysTalentProfileController {

    @Autowired
    private SysTalentProfileService SystalentProfileService;

    /**
     * 获取所有交叉人才档案列表
     */
    @GetMapping("/list")
    public Result getList() {
        // 使用 MyBatis-Plus 自带的 list() 方法查询所有数据
        List<SysTalentProfile> list = SystalentProfileService.list();
        return Result.success(list);
    }

}
