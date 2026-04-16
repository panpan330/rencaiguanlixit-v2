package com.rehab.managerv2.controller;

import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysUser;
// 规范：注入的是接口 ISysUserService，而不是实现类
import com.rehab.managerv2.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/sys-user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 🔥 登录接口：服务员只负责要 Token，具体怎么验明正身是后厨的事
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody SysUser loginUser) {
        String token = sysUserService.login(loginUser);
        if (token == null) {
            // 安全规范：登录失败时不明确告诉用户是账号错还是密码错，防止黑客暴力试探账号
            return Result.error(400, "账号或密码错误！");
        }
        return Result.success(token);
    }

    /**
     * 获取分页列表：一行代码搞定
     */
    @GetMapping("/list")
    public Result<Page<SysUser>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(sysUserService.page(new Page<>(pageNo, pageSize)));
    }

    /**
     * 🔥 新增接口：调用大厨专属的“带加密的新增方法”
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysUser sysUser) {
        return sysUserService.saveUserWithEncryption(sysUser) ?
                Result.success(true) : Result.error(500, "新增失败");
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SysUser sysUser) {
        return sysUserService.updateById(sysUser) ?
                Result.success(true) : Result.error(500, "修改失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        return sysUserService.removeById(id) ?
                Result.success(true) : Result.error(500, "删除失败");
    }
}