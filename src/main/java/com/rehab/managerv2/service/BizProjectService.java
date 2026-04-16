package com.rehab.managerv2.service;

import com.rehab.managerv2.entity.BizProject;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 科研/企业项目表 服务类
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
public interface BizProjectService extends IService<BizProject> {

    /**
     * 执行智能人才撮合核心算法 2.0
     * @param projectId 项目ID
     * @return 匹配成功的人才数量
     */
    int executeSmartMatch(Long projectId);

}
