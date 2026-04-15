package com.rehab.managerv2.controller;


import com.rehab.managerv2.entity.SysUser;
import com.rehab.managerv2.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 接口：获取所有用户列表
     * 访问路径: http://localhost:8080/sys/user/list
     */
    @GetMapping("/list")
    public List<SysUser> getUserList() {
        List<SysUser> list = sysUserService.list();
        // 加这行，在控制台看看查出来的 Java 对象有没有值
        System.out.println("查询到的数据内容是：" + list);
        return list;
    }

    /**
     * 1. 新增用户 (Create)
     * 使用 @PostMapping 处理 POST 请求
     * @RequestBody 表示把前端传来的 JSON 自动装配成 SysUser 对象
     */
    @PostMapping("/add")
    public boolean add(@RequestBody SysUser sysUser) {
        // MyBatis-Plus 自带的 save 方法，自动生成 INSERT 语句
        return sysUserService.save(sysUser);
    }

    /**
     * 2. 修改用户 (Update)
     * 使用 @PutMapping 处理 PUT 请求
     */
    @PutMapping("/update")
    public boolean update(@RequestBody SysUser sysUser) {
        // MyBatis-Plus 自带的 updateById 方法，根据主键 ID 自动生成 UPDATE 语句
        return sysUserService.updateById(sysUser);
    }

    /**
     * 3. 删除用户 (Delete)
     * 使用 @DeleteMapping 处理 DELETE 请求
     * @PathVariable 表示从 URL 路径中提取 {id} 的值
     */
    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id) {
        // MyBatis-Plus 自带的 removeById 方法，自动生成 DELETE 语句
        return sysUserService.removeById(id);
    }

}
