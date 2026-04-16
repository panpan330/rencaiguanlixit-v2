package com.rehab.managerv2.service;

import java.util.Map;

public interface DashboardService {
    /**
     * 获取全局统计数据
     * @return 包含各项人数的 Map
     */
    Map<String, Long> getGlobalStats();
}
