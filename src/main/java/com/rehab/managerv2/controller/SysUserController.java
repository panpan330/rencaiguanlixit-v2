package com.rehab.managerv2.controller;

import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysUser;
import com.rehab.managerv2.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

@RestController
@RequestMapping("/sys-user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    public Result<Page<SysUser>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 1. 构造分页参数对象
        Page<SysUser> pageParam = new Page<>(pageNo, pageSize);

        // 2. 调用 MyBatis-Plus 的分页查询方法
        Page<SysUser> resultPage = sysUserService.page(pageParam);

        // 3. 把查出来的分页结果包装进咱们的大厂包装盒里返回
        return Result.success(resultPage);
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysUser sysUser) {
        boolean flag = sysUserService.save(sysUser);
        return flag ? Result.success(true) : Result.error(500, "新增失败");
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SysUser sysUser) {
        boolean flag = sysUserService.updateById(sysUser);
        return flag ? Result.success(true) : Result.error(500, "修改失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean flag = sysUserService.removeById(id);
        return flag ? Result.success(true) : Result.error(500, "删除失败");
    }
}