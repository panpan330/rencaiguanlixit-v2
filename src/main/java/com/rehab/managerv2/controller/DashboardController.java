package com.rehab.managerv2.controller;

import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 首页数据大屏控制器（极简规范版）
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<Map<String, Long>> getStats() {
        // 服务员只负责点菜、端菜，不管后厨怎么切菜
        return Result.success(dashboardService.getGlobalStats());
    }
}