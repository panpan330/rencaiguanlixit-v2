package com.rehab.managerv2.service.impl;

import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.entity.SysTalentSkill;
import com.rehab.managerv2.mapper.SysTalentProfileMapper;
import com.rehab.managerv2.service.SysTalentProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rehab.managerv2.service.SysTalentSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 交叉人才基本档案表 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-04-15
 */
@Service
public class SysTalentProfileServiceImpl extends ServiceImpl<SysTalentProfileMapper, SysTalentProfile> implements SysTalentProfileService {
    @Autowired
    private SysTalentSkillService sysTalentSkillService; // 注入技能表服务

    @Override
    public List<Map<String, Object>> calculateSmartMatch(Map<String, Integer> weights) {
        // 1. 构建需求向量
        double[] userVector = {
                weights.getOrDefault("dev", 20),
                weights.getOrDefault("algo", 20),
                weights.getOrDefault("clinical", 20),
                weights.getOrDefault("physio", 20),
                weights.getOrDefault("hardware", 20)
        };

        // 2. 获取基础数据
        List<SysTalentProfile> profiles = this.list(); // 调用父类 IService 的方法
        List<SysTalentSkill> skills = sysTalentSkillService.list();
        List<Map<String, Object>> resultList = new ArrayList<>();

        // 3. 执行余弦相似度算法
        for (SysTalentProfile profile : profiles) {
            SysTalentSkill mySkill = skills.stream()
                    .filter(s -> s.getTalentId().equals(profile.getId()))
                    .findFirst().orElse(null);

            if (mySkill != null) {
                double[] talentVector = {
                        mySkill.getDevScore(), mySkill.getAlgoScore(),
                        mySkill.getClinicalScore(), mySkill.getPhysioScore(),
                        mySkill.getHardwareScore()
                };

                // 余弦相似度计算逻辑... (此处省略具体计算公式代码，与之前相同)
                int finalScore = calculateCosineSimilarity(userVector, talentVector);

                Map<String, Object> map = new HashMap<>();
                map.put("name", profile.getRealName());
                map.put("domain", formatDomain(profile.getPrimaryDomain()));
                map.put("desc", profile.getResearchDirection());
                map.put("strongPoints", mySkill.getStrongPoints());
                map.put("score", finalScore);
                resultList.add(map);
            }
        }

        // 4. 排序并截取 Top 3
        resultList.sort((a, b) -> (Integer) b.get("score") - (Integer) a.get("score"));
        return resultList.size() > 3 ? resultList.subList(0, 3) : resultList;
    }

    // 抽离出来的私有辅助方法，让代码更整洁
    private int calculateCosineSimilarity(double[] v1, double[] v2) {
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            normA += Math.pow(v1[i], 2);
            normB += Math.pow(v2[i], 2);
        }
        return (normA == 0 || normB == 0) ? 0 : (int) Math.round((dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))) * 100);
    }

    private String formatDomain(Integer domainId) {
        return domainId == 0 ? "计算机科学" : (domainId == 1 ? "康复医学" : "深度交叉");
    }
}

