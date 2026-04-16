package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.entity.*;
import com.rehab.managerv2.mapper.BizProjectMapper;
import com.rehab.managerv2.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 科研/企业项目表 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Service
public class BizProjectServiceImpl extends ServiceImpl<BizProjectMapper, BizProject> implements BizProjectService {
    @Autowired
    private BizProjectRequirementService requirementService;
    @Autowired
    private SysTalentSkillService skillService;
    @Autowired
    private BizMatchRecordService matchRecordService;
    @Autowired
    private SysNoticeService noticeService;
    @Autowired
    private SysTalentProfileService profileService;

    // 添加 @Transactional，保证如果发站内信失败了，前面的匹配记录也会回滚，保证数据绝对一致性！
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int executeSmartMatch(Long projectId) {
        // 1. 查询项目的能力需求门槛 (算法输入向量 A)
        QueryWrapper<BizProjectRequirement> reqWrapper = new QueryWrapper<>();
        reqWrapper.eq("project_id", projectId);
        BizProjectRequirement req = requirementService.getOne(reqWrapper);

        if (req == null) {
            throw new RuntimeException("该项目尚未配置能力需求，无法进行匹配");
        }
        double[] projectVector = {req.getDevReq(), req.getAlgoReq(), req.getClinicalReq(), req.getPhysioReq(), req.getHardwareReq()};

        // 2. 获取全库所有人才的技能分数 (算法输入向量 B 集合)
        List<SysTalentSkill> allSkills = skillService.list();
        List<Map<String, Object>> matchResults = new ArrayList<>();

        // 3. 执行核心算法：余弦相似度计算
        for (SysTalentSkill skill : allSkills) {
            double[] talentVector = {skill.getDevScore(), skill.getAlgoScore(), skill.getClinicalScore(), skill.getPhysioScore(), skill.getHardwareScore()};
            int score = calculateCosineSimilarity(projectVector, talentVector);

            // 只有匹配度及格（比如大于 60%）的人才，才进入候选池
            if (score >= 60) {
                Map<String, Object> map = new HashMap<>();
                map.put("talentId", skill.getTalentId());
                map.put("score", score);
                matchResults.add(map);
            }
        }

        // 4. 对候选池按分数从高到低排序，截取 Top 3
        matchResults.sort((a, b) -> (Integer) b.get("score") - (Integer) a.get("score"));
        List<Map<String, Object>> top3 = matchResults.size() > 3 ? matchResults.subList(0, 3) : matchResults;

        // 5. 结果落地：将 Top 3 写入匹配记录表，并发送站内信
        BizProject project = this.getById(projectId);

        for (Map<String, Object> result : top3) {
            Long talentId = (Long) result.get("talentId");
            int score = (Integer) result.get("score");

            // 5.1 写入 biz_match_record
            BizMatchRecord record = new BizMatchRecord();
            record.setProjectId(projectId);
            record.setTalentId(talentId);
            record.setMatchScore(score);
            record.setStatus(0); // 0-已推荐
            matchRecordService.save(record);

            // 5.2 触发系统站内信 (sys_notice)
            SysTalentProfile profile = profileService.getById(talentId);
            SysNotice notice = new SysNotice();
            notice.setNoticeTitle("【智能推荐】您有一个高匹配度项目待查看");
            notice.setNoticeContent(String.format("尊敬的 %s，系统通过 AI 算法为您匹配了项目【%s】，您的综合契合度高达 %d%%，请尽快查看项目详情并决定是否接取！",
                    profile != null ? profile.getRealName() : "专家", project.getProjectName(), score));
            // 如果你的 userId 已经做好了关联，这里可以存 userId。目前先存 talentId 作为示意
            notice.setReceiverId(talentId);
            noticeService.save(notice);
        }

        // 返回成功匹配到了几个人
        return top3.size();
    }

    // 私有辅助方法：计算余弦相似度
    private int calculateCosineSimilarity(double[] v1, double[] v2) {
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            normA += Math.pow(v1[i], 2);
            normB += Math.pow(v2[i], 2);
        }
        return (normA == 0 || normB == 0) ? 0 : (int) Math.round((dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))) * 100);
    }
}


