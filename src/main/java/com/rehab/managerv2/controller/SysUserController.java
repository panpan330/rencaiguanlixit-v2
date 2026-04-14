package com.rehab.managerv2.controller;


import com.rehab.managerv2.entity.SysUser;
import com.rehab.managerv2.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author author
 * @since 2026-04-14
 */
@RestController
@RequestMapping("/sys-user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 测试接口：获取所有用户列表
     * 访问路径: http://localhost:8080/sys/user/list
     */
    @GetMapping("/list")
    public List<SysUser> getUserList() {
        List<SysUser> list = sysUserService.list();
        // 加这行，在控制台看看查出来的 Java 对象有没有值
        System.out.println("查询到的数据内容是：" + list);
        return list;
    }

}
