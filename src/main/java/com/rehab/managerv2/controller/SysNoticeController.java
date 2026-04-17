package com.rehab.managerv2.controller;


import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysNotice;
import com.rehab.managerv2.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 通知公告表 前端控制器
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@RestController
@RequestMapping("/sys-notice")
public class SysNoticeController {
    @Autowired
    private SysNoticeService noticeService;

    @GetMapping("/list")
    public Result<List<SysNotice>> getList(@RequestParam(required = false) Long talentId,
                                           @RequestParam(required = false) Long userId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysNotice> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        queryWrapper.and(wrapper -> {
            wrapper.isNull("receiver_id"); // 广播消息所有人可见
            if (talentId != null) {
                wrapper.or().eq("receiver_id", talentId);
            }
            if (userId != null) {
                // 根据 userId 查询，如果是发给该专家的通知，通常关联的是 userId
                wrapper.or().eq("receiver_id", userId);
            }
        });
        
        queryWrapper.orderByDesc("create_time");
        return Result.success(noticeService.list(queryWrapper));
    }

    @PutMapping("/read/{id}")
    public Result markAsRead(@PathVariable Long id) {
        // 服务员只负责转达更新指令
        return noticeService.markAsRead(id) ?
                Result.success("已读") : Result.error(500, "操作失败");
    }


}
