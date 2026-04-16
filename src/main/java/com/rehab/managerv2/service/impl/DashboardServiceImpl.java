package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.service.DashboardService;
import com.rehab.managerv2.service.SysTalentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    // 聚合服务可以注入其他业务的 Service！
    @Autowired
    private SysTalentProfileService profileService;

    @Override
    public Map<String, Long> getGlobalStats() {
        Map<String, Long> stats = new HashMap<>();

        // 所有的 QueryWrapper (切菜逻辑) 全部封装在 Service 层
        stats.put("total", profileService.count());

        stats.put("csCount", profileService.count(
                new QueryWrapper<SysTalentProfile>().eq("primary_domain", 0)));

        stats.put("rehabCount", profileService.count(
                new QueryWrapper<SysTalentProfile>().eq("primary_domain", 1)));

        stats.put("crossCount", profileService.count(
                new QueryWrapper<SysTalentProfile>().eq("primary_domain", 2)));

        return stats;
    }
}