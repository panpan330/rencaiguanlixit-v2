package com.rehab.managerv2.service;

import com.rehab.managerv2.entity.BizMatchRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface BizMatchRecordService extends IService<BizMatchRecord> {

    /**
     * 根据项目ID，自动计算人才匹配度并生成推荐记录
     * @param projectId 项目ID
     * @return 匹配记录列表
     */
    List<BizMatchRecord> autoMatchTalents(Long projectId);
}