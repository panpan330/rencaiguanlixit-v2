package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.entity.SysTalentSkill;
import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.entity.BizTalentEducation;
import com.rehab.managerv2.entity.BizTalentExperience;
import com.rehab.managerv2.entity.BizTalentAchievement;
import com.rehab.managerv2.mapper.SysTalentSkillMapper;
import com.rehab.managerv2.service.SysTalentSkillService;
import com.rehab.managerv2.service.SysTalentProfileService;
import com.rehab.managerv2.service.BizTalentEducationService;
import com.rehab.managerv2.service.BizTalentExperienceService;
import com.rehab.managerv2.service.BizTalentAchievementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 交叉人才技能雷达分数表 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Service
public class SysTalentSkillServiceImpl extends ServiceImpl<SysTalentSkillMapper, SysTalentSkill> implements SysTalentSkillService {

    @Autowired
    private SysTalentProfileService sysTalentProfileService;
    @Autowired
    private BizTalentEducationService bizTalentEducationService;
    @Autowired
    private BizTalentExperienceService bizTalentExperienceService;
    @Autowired
    private BizTalentAchievementService bizTalentAchievementService;

    @Override
    public SysTalentSkill getSkillByTalentId(Long talentId) {
        // 所有的数据库查询条件拼装，严禁离开 Service 层！
        QueryWrapper<SysTalentSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("talent_id", talentId);
        return this.getOne(queryWrapper);
    }

    @Override
    public SysTalentSkill aiEvaluateSkill(Long talentId) {
        SysTalentProfile profile = sysTalentProfileService.getById(talentId);
        if (profile == null) return null;

        int dev = 50, algo = 50, clinical = 50, physio = 50, hardware = 50;

        // 1. 基础画像加分 (最高学历与领域)
        String eduLevel = profile.getEducationLevel() == null ? "" : profile.getEducationLevel();
        int baseScore = 0;
        if (eduLevel.contains("博士")) baseScore = 20;
        else if (eduLevel.contains("硕士")) baseScore = 10;
        else if (eduLevel.contains("本科")) baseScore = 5;
        
        dev += baseScore; algo += baseScore; clinical += baseScore; physio += baseScore; hardware += baseScore;

        Integer domain = profile.getPrimaryDomain();
        if (domain != null) {
            if (domain == 0) { dev += 15; algo += 15; } // 计算机科学
            else if (domain == 1) { clinical += 20; physio += 15; } // 康复医学
            else if (domain == 2) { algo += 10; hardware += 10; physio += 10; } // 深度交叉
        }

        // 2. 文本关键词分析 (NLP Tagging)
        String fullText = profile.getResearchDirection() == null ? "" : profile.getResearchDirection();

        // 提取教育经历关键词
        QueryWrapper<BizTalentEducation> eduQw = new QueryWrapper<>();
        eduQw.eq("talent_id", talentId);
        List<BizTalentEducation> edus = bizTalentEducationService.list(eduQw);
        for (BizTalentEducation e : edus) {
            fullText += " " + (e.getMajor() == null ? "" : e.getMajor());
        }

        // 提取项目/工作经历关键词
        QueryWrapper<BizTalentExperience> expQw = new QueryWrapper<>();
        expQw.eq("talent_id", talentId);
        List<BizTalentExperience> exps = bizTalentExperienceService.list(expQw);
        for (BizTalentExperience e : exps) {
            fullText += " " + (e.getOrganization() == null ? "" : e.getOrganization());
            fullText += " " + (e.getPosition() == null ? "" : e.getPosition());
            fullText += " " + (e.getDescription() == null ? "" : e.getDescription());
        }

        // 提取科研成果关键词
        QueryWrapper<BizTalentAchievement> achQw = new QueryWrapper<>();
        achQw.eq("talent_id", talentId);
        List<BizTalentAchievement> achs = bizTalentAchievementService.list(achQw);
        for (BizTalentAchievement a : achs) {
            fullText += " " + (a.getTitle() == null ? "" : a.getTitle());
            
            // 成果额外加分
            if (a.getType() != null) {
                if (a.getType() == 0) { algo += 5; clinical += 5; } // SCI论文
                else if (a.getType() == 1) { hardware += 8; dev += 5; } // 发明专利
                else if (a.getType() == 2) { dev += 10; } // 软著
            }
        }

        fullText = fullText.toLowerCase();

        // 关键词匹配加分引擎
        if (fullText.contains("java") || fullText.contains("python") || fullText.contains("c++") || fullText.contains("开发") || fullText.contains("软件")) dev += 15;
        if (fullText.contains("前端") || fullText.contains("vue") || fullText.contains("react") || fullText.contains("后端") || fullText.contains("微服务")) dev += 10;

        if (fullText.contains("算法") || fullText.contains("深度学习") || fullText.contains("机器学习") || fullText.contains("神经网络") || fullText.contains("模型")) algo += 20;
        if (fullText.contains("聚类") || fullText.contains("分类") || fullText.contains("预测") || fullText.contains("数据挖掘") || fullText.contains("nlp")) algo += 10;

        if (fullText.contains("临床") || fullText.contains("患者") || fullText.contains("评估") || fullText.contains("康复") || fullText.contains("治疗")) clinical += 20;
        if (fullText.contains("医院") || fullText.contains("医学") || fullText.contains("病理") || fullText.contains("干预") || fullText.contains("试验")) clinical += 10;

        if (fullText.contains("生理") || fullText.contains("脑电") || fullText.contains("肌电") || fullText.contains("神经") || fullText.contains("生物")) physio += 20;
        if (fullText.contains("eeg") || fullText.contains("emg") || fullText.contains("信号") || fullText.contains("解剖")) physio += 10;

        if (fullText.contains("硬件") || fullText.contains("嵌入式") || fullText.contains("单片机") || fullText.contains("电路") || fullText.contains("传感器")) hardware += 20;
        if (fullText.contains("机器人") || fullText.contains("外骨骼") || fullText.contains("采集设备") || fullText.contains("pcb") || fullText.contains("控制")) hardware += 10;

        // 分数钳制 (Clamp to 0-100)
        dev = Math.min(100, Math.max(0, dev));
        algo = Math.min(100, Math.max(0, algo));
        clinical = Math.min(100, Math.max(0, clinical));
        physio = Math.min(100, Math.max(0, physio));
        hardware = Math.min(100, Math.max(0, hardware));

        // 组装结果
        SysTalentSkill skill = new SysTalentSkill();
        skill.setTalentId(talentId);
        skill.setDevScore(dev);
        skill.setAlgoScore(algo);
        skill.setClinicalScore(clinical);
        skill.setPhysioScore(physio);
        skill.setHardwareScore(hardware);
        skill.setStrongPoints("【智能评估分析】结合人才的学历、研究方向、教育经历、项目经验及科研成果自动生成");

        return skill;
    }
}
