package com.rehab.managerv2.service;

import java.util.Map;

public interface DashboardService {

    /**
     * 获取大屏核心统计数据
     */
    Map<String, Object> getGlobalStats();
}
