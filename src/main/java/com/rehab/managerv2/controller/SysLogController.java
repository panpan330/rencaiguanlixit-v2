package com.rehab.managerv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysLog;
import com.rehab.managerv2.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys-log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @GetMapping("/page")
    public Result<Page<SysLog>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation) {
        
        QueryWrapper<SysLog> queryWrapper = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (operation != null && !operation.isEmpty()) {
            queryWrapper.like("operation", operation);
        }
        queryWrapper.orderByDesc("create_time");

        Page<SysLog> page = new Page<>(current, size);
        return Result.success(sysLogService.page(page, queryWrapper));
    }
}
