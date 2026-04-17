package com.rehab.managerv2.controller;


import com.alibaba.excel.EasyExcel;
import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.BizMatchRecord;
import com.rehab.managerv2.entity.MatchReportExportDTO;
import com.rehab.managerv2.service.BizMatchRecordService;
import com.rehab.managerv2.service.SysTalentProfileService;
import com.rehab.managerv2.service.SysTalentSkillService;
import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.entity.SysTalentSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 智能撮合历史记录表 前端控制器
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
import com.rehab.managerv2.websocket.NotificationWebSocket;
import com.rehab.managerv2.entity.BizProject;
import com.rehab.managerv2.service.BizProjectService;

import com.rehab.managerv2.entity.SysNotice;
import com.rehab.managerv2.service.SysNoticeService;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/biz-match-record")
public class BizMatchRecordController {

    @Autowired
    private BizProjectService projectService;

    @Autowired
    private BizMatchRecordService matchRecordService;

    @Autowired
    private SysTalentProfileService profileService;

    @Autowired
    private SysTalentSkillService skillService;

    @Autowired
    private SysNoticeService noticeService;

    /**
     * 根据项目ID获取匹配推荐记录
     */
    @GetMapping("/list/{projectId}")
    public Result<List<BizMatchRecord>> getListByProject(@PathVariable Long projectId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BizMatchRecord> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(BizMatchRecord::getProjectId, projectId)
               .orderByAsc(BizMatchRecord::getStatus) // 状态优先：待邀请(0)最上面，其次(1)，再其次已合作(3)等
               .orderByDesc(BizMatchRecord::getMatchScore) // 同状态下，匹配度高的排前面
               .orderByDesc(BizMatchRecord::getCreateTime); // 兜底：最新生成的排前面
        
        List<BizMatchRecord> records = matchRecordService.list(wrapper);
        // 简单拼装一下人才的姓名，方便前端展示
        if (!records.isEmpty()) {
            com.rehab.managerv2.service.SysTalentProfileService profileService = 
                com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.SysTalentProfileService.class);
            com.rehab.managerv2.service.BizProjectService projectService = 
                com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.BizProjectService.class);
            
            for (BizMatchRecord record : records) {
                com.rehab.managerv2.entity.SysTalentProfile profile = profileService.getById(record.getTalentId());
                if (profile != null) {
                    record.setTalentName(profile.getRealName());
                }
                com.rehab.managerv2.entity.BizProject project = projectService.getById(record.getProjectId());
                if (project != null) {
                    record.setProjectName(project.getProjectName());
                }
            }
        }
        return Result.success(records);
    }

    /**
     * 根据人才ID获取他的匹配推荐记录
     */
    @GetMapping("/list-talent/{talentId}")
    public Result<List<BizMatchRecord>> getListByTalent(@PathVariable Long talentId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BizMatchRecord> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(BizMatchRecord::getTalentId, talentId)
               .orderByDesc(BizMatchRecord::getCreateTime);
        
        List<BizMatchRecord> records = matchRecordService.list(wrapper);
        if (!records.isEmpty()) {
            com.rehab.managerv2.service.BizProjectService projectService = 
                com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.BizProjectService.class);
            for (BizMatchRecord record : records) {
                com.rehab.managerv2.entity.BizProject project = projectService.getById(record.getProjectId());
                if (project != null) {
                    record.setProjectName(project.getProjectName());
                }
            }
        }
        return Result.success(records);
    }

    @PostMapping("/autoMatch/{projectId}")
    public Result autoMatch(@PathVariable Long projectId) {
        try {
            // 调用我们刚才写的算法
            List<BizMatchRecord> records = matchRecordService.autoMatchTalents(projectId);
            return Result.success(records);
        } catch (RuntimeException e) {
            // 捕获我们在 Service 中抛出的业务异常，传入状态码 500
            return Result.error(500, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // 系统级别异常，同样传入状态码 500
            return Result.error(500, "系统异常，撮合失败");
        }
    }

    /**
     * 项目方主动发送邀请通知给推荐的人才
     */
    @PostMapping("/send-invite/{recordId}")
    public Result sendInvite(@PathVariable Long recordId) {
        try {
            matchRecordService.sendInvitation(recordId);
            return Result.success("邀请发送成功，人才将收到系统通知！");
        } catch (Exception e) {
            return Result.error(500, "发送邀请失败：" + e.getMessage());
        }
    }

    /**
     * 人才端：接受/拒绝邀请
     */
    @PostMapping("/handle-invite/{recordId}")
    public Result handleInvite(@PathVariable Long recordId, @RequestParam Integer status) {
        // status: 2-拒绝, 3-已合作(接受)
        try {
            matchRecordService.handleInvitation(recordId, status);
            return Result.success("操作成功！");
        } catch (Exception e) {
            return Result.error(500, "操作失败：" + e.getMessage());
        }
    }

    /**
     * 项目方：取消与人才的合作
     */
    @PostMapping("/cancel-cooperation/{recordId}")
    public Result cancelCooperation(@PathVariable Long recordId) {
        try {
            matchRecordService.cancelCooperation(recordId);
            return Result.success("已成功取消合作，并已通知该人才！");
        } catch (Exception e) {
            return Result.error(500, "操作失败：" + e.getMessage());
        }
    }

    @PostMapping("/update-status/{id}")
    public Result updateStatus(@PathVariable Long id, Integer status) {
        BizMatchRecord record = matchRecordService.getById(id);
        if (record == null) return Result.error(500, "记录不存在");

        record.setStatus(status);
        boolean success = matchRecordService.updateById(record);
        return success ? Result.success("状态更新成功") : Result.error(500, "更新失败");
    }

    /**
     * 导出项目智能推荐人才报告 (Excel)
     */
    @GetMapping("/export/{projectId}")
    public void exportReport(@PathVariable Long projectId, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("项目撮合推荐报告_" + projectId, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<BizMatchRecord> records = matchRecordService.getRecordsByProjectId(projectId);
        List<MatchReportExportDTO> exportList = new ArrayList<>();
        
        int rank = 1;
        for (BizMatchRecord record : records) {
            MatchReportExportDTO dto = new MatchReportExportDTO();
            dto.setRank(rank++);
            dto.setTalentName(record.getTalentName());
            dto.setMatchScore(record.getMatchScore() + "%");

            SysTalentProfile profile = profileService.getById(record.getTalentId());
            if (profile != null) {
                dto.setPhone(profile.getPhone() == null ? "未留存" : profile.getPhone());
                dto.setEducationLevel(profile.getEducationLevel() == null ? "未知" : profile.getEducationLevel());
            }

            SysTalentSkill skill = skillService.getSkillByTalentId(record.getTalentId());
            if (skill != null) {
                dto.setDevScore(skill.getDevScore());
                dto.setAlgoScore(skill.getAlgoScore());
                dto.setClinicalScore(skill.getClinicalScore());
                dto.setPhysioScore(skill.getPhysioScore());
                dto.setHardwareScore(skill.getHardwareScore());
                dto.setStrongPoints(skill.getStrongPoints());
            }

            exportList.add(dto);
        }

        EasyExcel.write(response.getOutputStream(), MatchReportExportDTO.class).sheet("AI推荐人才列表").doWrite(exportList);
    }
}
