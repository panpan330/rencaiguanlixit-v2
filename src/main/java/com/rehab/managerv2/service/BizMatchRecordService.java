package com.rehab.managerv2.service;

import com.rehab.managerv2.entity.BizMatchRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface BizMatchRecordService extends IService<BizMatchRecord> {
    
    /**
     * 为项目智能匹配候选人
     */
    List<BizMatchRecord> autoMatchTalents(Long projectId);

    /**
     * 获取项目的候选人记录
     */
    List<BizMatchRecord> getRecordsByProjectId(Long projectId);

    /**
     * 项目方主动发送邀请给推荐的人才
     */
    void sendInvitation(Long recordId);

    /**
     * 人才接受或拒绝邀请
     */
    void handleInvitation(Long recordId, Integer actionStatus);

    /**
     * 项目方取消与人才的合作
     */
    void cancelCooperation(Long recordId);
}