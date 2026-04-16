package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rehab.managerv2.entity.BizMatchRecord;
import com.rehab.managerv2.entity.BizProjectRequirement;
import com.rehab.managerv2.entity.SysTalentSkill;
import com.rehab.managerv2.mapper.BizMatchRecordMapper;
import com.rehab.managerv2.mapper.BizProjectRequirementMapper;
import com.rehab.managerv2.mapper.SysTalentSkillMapper;
import com.rehab.managerv2.service.BizMatchRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BizMatchRecordServiceImpl extends ServiceImpl<BizMatchRecordMapper, BizMatchRecord> implements BizMatchRecordService {

    @Autowired
    private BizProjectRequirementMapper requirementMapper;

    @Autowired
    private SysTalentSkillMapper talentSkillMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BizMatchRecord> autoMatchTalents(Long projectId) {
        // 1. 查询项目的具体需求
        LambdaQueryWrapper<BizProjectRequirement> reqWrapper = new LambdaQueryWrapper<>();
        reqWrapper.eq(BizProjectRequirement::getProjectId, projectId);
        BizProjectRequirement requirement = requirementMapper.selectOne(reqWrapper);

        if (requirement == null) {
            throw new RuntimeException("该项目尚未发布能力需求指标，无法进行匹配");
        }

        // 2. 获取所有人才的技能画像分数
        List<SysTalentSkill> allTalents = talentSkillMapper.selectList(null);
        List<BizMatchRecord> matchRecords = new ArrayList<>();

        // 3. 核心算法：遍历人才，计算匹配度
        for (SysTalentSkill talent : allTalents) {
            int score = calculateMatchScore(talent, requirement);

            // 设定一个及格线，比如匹配度大于 60 分才算有效推荐
            if (score >= 60) {
                BizMatchRecord record = new BizMatchRecord();
                record.setProjectId(projectId);
                record.setTalentId(talent.getTalentId());
                record.setMatchScore(score);
                record.setStatus(0); // 0-已推荐
                record.setCreateTime(LocalDateTime.now());
                matchRecords.add(record);
            }
        }

        // 4. 按照匹配分数从高到低排序
        matchRecords.sort(Comparator.comparing(BizMatchRecord::getMatchScore).reversed());

        // 5. 存入数据库前，先清除该项目历史的"未处理"推荐记录（防止重复推荐）
        LambdaQueryWrapper<BizMatchRecord> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BizMatchRecord::getProjectId, projectId)
                .eq(BizMatchRecord::getStatus, 0); // 只删已推荐但还没后续操作的
        this.remove(deleteWrapper);

        // 6. 批量保存新的推荐记录
        if (!matchRecords.isEmpty()) {
            this.saveBatch(matchRecords);
        }

        return matchRecords;
    }

    /**
     * 内部方法：计算五维雷达匹配分数
     */
    private int calculateMatchScore(SysTalentSkill talent, BizProjectRequirement req) {
        double devRatio = getDimensionRatio(talent.getDevScore(), req.getDevReq());
        double algoRatio = getDimensionRatio(talent.getAlgoScore(), req.getAlgoReq());
        double clinicalRatio = getDimensionRatio(talent.getClinicalScore(), req.getClinicalReq());
        double physioRatio = getDimensionRatio(talent.getPhysioScore(), req.getPhysioReq());
        double hardRatio = getDimensionRatio(talent.getHardwareScore(), req.getHardwareReq());

        // 计算平均满足率，转换为百分制整数
        double totalRatio = (devRatio + algoRatio + clinicalRatio + physioRatio + hardRatio) / 5.0;
        return (int) Math.round(totalRatio * 100);
    }

    /**
     * 内部方法：计算单个维度的满足率 (最高1.0)
     */
    private double getDimensionRatio(Integer talentScore, Integer reqScore) {
        if (talentScore == null) talentScore = 0;
        if (reqScore == null || reqScore == 0) return 1.0; // 如果项目对该项无要求，视为100%满足

        if (talentScore >= reqScore) {
            return 1.0; // 能力溢出不加分，满分1.0
        } else {
            return (double) talentScore / reqScore; // 能力不足则按比例折算
        }
    }
}