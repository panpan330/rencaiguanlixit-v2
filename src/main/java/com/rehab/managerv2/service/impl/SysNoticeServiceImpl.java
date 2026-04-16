package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.entity.SysNotice;
import com.rehab.managerv2.mapper.SysNoticeMapper;
import com.rehab.managerv2.service.SysNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 通知公告表 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {
    @Override
    public List<SysNotice> getNoticeList() {
        // 找菜的逻辑，严禁离开 Service 层！
        QueryWrapper<SysNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public boolean markAsRead(Long id) {
        // 拼装数据的逻辑，严禁离开 Service 层！
        SysNotice notice = new SysNotice();
        notice.setNoticeId(id);
        notice.setStatus(1); // 1 代表已读
        return this.updateById(notice);
    }

}
