package com.rehab.managerv2.service;

import com.rehab.managerv2.entity.SysNotice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 通知公告表 服务类
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
public interface SysNoticeService extends IService<SysNotice> {

    /**
     * 获取当前系统所有的通知（按时间倒序排）
     */
    List<SysNotice> getNoticeList();

    /**
     * 将消息标记为已读
     */
    boolean markAsRead(Long id);

}
