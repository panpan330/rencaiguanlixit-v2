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
import com.rehab.managerv2.entity.SysNotice;
import com.rehab.managerv2.entity.BizProject;
import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.mapper.BizProjectMapper;
import com.rehab.managerv2.mapper.SysNoticeMapper;
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
    
    @Autowired
    private BizProjectMapper projectMapper;
    
    @Autowired
    private SysNoticeMapper noticeMapper;

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

        // 2. 获取所有人才的技能画像分数 (需要排除已经被删除的人才或者不存在的人才)
        List<SysTalentSkill> allTalents = talentSkillMapper.selectList(null);
        
        com.rehab.managerv2.service.SysTalentProfileService profileService = 
            com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.SysTalentProfileService.class);
            
        // 过滤出有效的 talentId 列表（没被删除的档案）
        List<Long> validTalentIds = profileService.list().stream()
                .map(com.rehab.managerv2.entity.SysTalentProfile::getId)
                .collect(Collectors.toList());

        // 3. 查询当前项目【已经有过交互历史】的人才ID集合 (status > 0：1-已邀请，2-已拒绝，3-已合作，4-已取消)
        LambdaQueryWrapper<BizMatchRecord> historyWrapper = new LambdaQueryWrapper<>();
        historyWrapper.eq(BizMatchRecord::getProjectId, projectId)
                      .gt(BizMatchRecord::getStatus, 0);
        List<Long> excludeTalentIds = this.list(historyWrapper).stream()
                .map(BizMatchRecord::getTalentId)
                .collect(Collectors.toList());

        List<BizMatchRecord> matchRecords = new ArrayList<>();

        // 4. 核心算法：遍历人才，计算匹配度
        for (SysTalentSkill talent : allTalents) {
            // 如果档案已被删除或者根本不存在，跳过
            if (!validTalentIds.contains(talent.getTalentId())) {
                continue;
            }
            
            // 如果这个人已经有交互历史（发过邀请等），直接跳过，不再重新推荐
            if (excludeTalentIds.contains(talent.getTalentId())) {
                continue;
            }

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

        // 5. 按照匹配分数从高到低排序
        matchRecords.sort(Comparator.comparing(BizMatchRecord::getMatchScore).reversed());

        // 6. 存入数据库前，先清除该项目历史的"未处理"推荐记录（防止重复推荐导致旧数据堆积）
        LambdaQueryWrapper<BizMatchRecord> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BizMatchRecord::getProjectId, projectId)
                .eq(BizMatchRecord::getStatus, 0); // 只删已推荐但还没后续操作的
        this.remove(deleteWrapper);

        // 7. 批量保存新的推荐记录
        if (!matchRecords.isEmpty()) {
            this.saveBatch(matchRecords);
        }

        return matchRecords;
    }

    @Override
    public List<BizMatchRecord> getRecordsByProjectId(Long projectId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BizMatchRecord> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("project_id", projectId);
        qw.orderByDesc("match_score");
        return this.list(qw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendInvitation(Long recordId) {
        BizMatchRecord record = this.getById(recordId);
        if (record == null) {
            throw new RuntimeException("未找到推荐记录");
        }
        if (record.getStatus() != 0) {
            throw new RuntimeException("该记录不是待邀请状态");
        }

        BizProject project = projectMapper.selectById(record.getProjectId());
        
        // 1. 发送通知给学生
        SysNotice notice = new SysNotice();
        notice.setNoticeTitle("【项目邀请】项目方邀请您参与项目");
        notice.setNoticeContent("项目【" + (project != null ? project.getProjectName() : "未知项目") + "】的项目方觉得您非常合适，现邀请您参与。请在个人主页的项目列表中确认是否接受。");
        notice.setReceiverId(record.getTalentId());
        notice.setStatus(0);
        notice.setCreateTime(LocalDateTime.now());
        noticeMapper.insert(notice);

        // 2. 发送通知给人才归属专家
        SysTalentProfile profile = com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.SysTalentProfileService.class).getById(record.getTalentId());
        if (profile != null && profile.getUserId() != null && project != null) {
            String msg = "【系统通知】您录入的人才 [" + profile.getRealName() + "] 收到了项目 [" + project.getProjectName() + "] 的撮合邀请！";
            SysNotice expertNotice = new SysNotice();
            expertNotice.setNoticeTitle("系统消息");
            expertNotice.setNoticeContent(msg);
            expertNotice.setReceiverId(profile.getUserId());
            expertNotice.setStatus(0);
            expertNotice.setCreateTime(LocalDateTime.now());
            noticeMapper.insert(expertNotice);
            com.rehab.managerv2.websocket.NotificationWebSocket.sendMessage(String.valueOf(profile.getUserId()), msg);
        }

        // 3. 更新匹配记录状态为"已发邀请"
        record.setStatus(1); // 1-已发送邀请
        this.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleInvitation(Long recordId, Integer actionStatus) {
        BizMatchRecord record = this.getById(recordId);
        if (record == null) {
            throw new RuntimeException("未找到记录");
        }
        // actionStatus: 2-拒绝, 3-接受/合作
        if (actionStatus != 2 && actionStatus != 3) {
            throw new RuntimeException("非法的处理状态");
        }
        record.setStatus(actionStatus);
        this.updateById(record);

        // 通知项目方
        BizProject project = projectMapper.selectById(record.getProjectId());
        SysTalentProfile profile = com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.SysTalentProfileService.class).getById(record.getTalentId());
        
        if (project != null && project.getUserId() != null && profile != null) {
            SysNotice notice = new SysNotice();
            notice.setNoticeTitle(actionStatus == 3 ? "【合作达成】人才已接受邀请" : "【合作被拒】人才已拒绝邀请");
            notice.setNoticeContent("您发布的项目【" + project.getProjectName() + "】，候选人 [" + profile.getRealName() + "] " + (actionStatus == 3 ? "已接受您的邀请！" : "婉拒了您的邀请。"));
            notice.setReceiverId(project.getUserId());
            notice.setStatus(0);
            notice.setCreateTime(LocalDateTime.now());
            noticeMapper.insert(notice);
            
            // WebSocket 实时推送
            com.rehab.managerv2.websocket.NotificationWebSocket.sendMessage(String.valueOf(project.getUserId()), notice.getNoticeContent());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCooperation(Long recordId) {
        BizMatchRecord record = this.getById(recordId);
        if (record == null) {
            throw new RuntimeException("未找到记录");
        }
        if (record.getStatus() != 3) {
            throw new RuntimeException("当前非合作状态，无法取消");
        }

        BizProject project = projectMapper.selectById(record.getProjectId());

        // 1. 发送通知告诉学生
        SysNotice notice = new SysNotice();
        notice.setNoticeTitle("【项目通知】项目合作已取消");
        notice.setNoticeContent("项目【" + (project != null ? project.getProjectName() : "未知项目") + "】的项目方已取消与您的合作。");
        notice.setReceiverId(record.getTalentId());
        notice.setStatus(0);
        notice.setCreateTime(LocalDateTime.now());
        noticeMapper.insert(notice);

        // 2. 发送通知给人才归属专家
        SysTalentProfile profile = com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.SysTalentProfileService.class).getById(record.getTalentId());
        if (profile != null && profile.getUserId() != null && project != null) {
            String msg = "【系统通知】您录入的人才 [" + profile.getRealName() + "] 被项目 [" + project.getProjectName() + "] 的项目方取消了合作。";
            SysNotice expertNotice = new SysNotice();
            expertNotice.setNoticeTitle("系统消息");
            expertNotice.setNoticeContent(msg);
            expertNotice.setReceiverId(profile.getUserId());
            expertNotice.setStatus(0);
            expertNotice.setCreateTime(LocalDateTime.now());
            noticeMapper.insert(expertNotice);
            com.rehab.managerv2.websocket.NotificationWebSocket.sendMessage(String.valueOf(profile.getUserId()), msg);
        }

        // 3. 更新状态为 4-已取消合作
        record.setStatus(4);
        this.updateById(record);
    }

    /**
     * 内部方法：计算五维雷达匹配分数，增加硬性门槛过滤
     * 如果某项能力不达标（低于设定门槛），直接淘汰（返回-1）
     */
    private int calculateMatchScore(SysTalentSkill talent, BizProjectRequirement req) {
        // 1. 硬性门槛一票否决制过滤
        if (talent.getDevScore() == null || talent.getDevScore() < (req.getDevReq() == null ? 0 : req.getDevReq())) return -1;
        if (talent.getAlgoScore() == null || talent.getAlgoScore() < (req.getAlgoReq() == null ? 0 : req.getAlgoReq())) return -1;
        if (talent.getClinicalScore() == null || talent.getClinicalScore() < (req.getClinicalReq() == null ? 0 : req.getClinicalReq())) return -1;
        if (talent.getPhysioScore() == null || talent.getPhysioScore() < (req.getPhysioReq() == null ? 0 : req.getPhysioReq())) return -1;
        if (talent.getHardwareScore() == null || talent.getHardwareScore() < (req.getHardwareReq() == null ? 0 : req.getHardwareReq())) return -1;

        // 2. 计算综合契合度分数（满足门槛的情况下）
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