package com.rehab.managerv2.controller;

import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysUser;
import com.rehab.managerv2.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys-user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    public Result<List<SysUser>> getUserList() {
        // 把查出来的列表塞进 success 包装盒
        return Result.success(sysUserService.list());
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