package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.entity.SysTalentSkill;
import com.rehab.managerv2.service.DashboardService;
import com.rehab.managerv2.service.SysTalentProfileService;
import com.rehab.managerv2.service.SysTalentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private SysTalentProfileService profileService;

    @Autowired
    private SysTalentSkillService skillService;

    @Override
    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", profileService.count());

        stats.put("csCount", profileService.count(
                new QueryWrapper<SysTalentProfile>().eq("primary_domain", 0)));

        stats.put("rehabCount", profileService.count(
                new QueryWrapper<SysTalentProfile>().eq("primary_domain", 1)));

        stats.put("crossCount", profileService.count(
                new QueryWrapper<SysTalentProfile>().eq("primary_domain", 2)));

        stats.put("schoolCount", profileService.count(
                new QueryWrapper<SysTalentProfile>().eq("employment_status", 0)));
        stats.put("researchCount", profileService.count(
                new QueryWrapper<SysTalentProfile>().eq("employment_status", 1)));
        stats.put("enterpriseCount", profileService.count(
                new QueryWrapper<SysTalentProfile>().eq("employment_status", 2)));

        // 计算所有人才的技能平均分，用于大屏雷达图
        List<SysTalentSkill> allSkills = skillService.list();
        long totalDev = 0, totalAlgo = 0, totalClinical = 0, totalPhysio = 0, totalHardware = 0;
        int count = allSkills.size();

        if (count > 0) {
            for (SysTalentSkill skill : allSkills) {
                totalDev += (skill.getDevScore() != null ? skill.getDevScore() : 0);
                totalAlgo += (skill.getAlgoScore() != null ? skill.getAlgoScore() : 0);
                totalClinical += (skill.getClinicalScore() != null ? skill.getClinicalScore() : 0);
                totalPhysio += (skill.getPhysioScore() != null ? skill.getPhysioScore() : 0);
                totalHardware += (skill.getHardwareScore() != null ? skill.getHardwareScore() : 0);
            }
            stats.put("avgDev", totalDev / count);
            stats.put("avgAlgo", totalAlgo / count);
            stats.put("avgClinical", totalClinical / count);
            stats.put("avgPhysio", totalPhysio / count);
            stats.put("avgHardware", totalHardware / count);
        } else {
            stats.put("avgDev", 0);
            stats.put("avgAlgo", 0);
            stats.put("avgClinical", 0);
            stats.put("avgPhysio", 0);
            stats.put("avgHardware", 0);
        }

        return stats;
    }
}