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
    public Result<List<SysNotice>> getList() {
        // 服务员只负责向大厨要菜
        return Result.success(noticeService.getNoticeList());
    }

    @PutMapping("/read/{id}")
    public Result markAsRead(@PathVariable Long id) {
        // 服务员只负责转达更新指令
        return noticeService.markAsRead(id) ?
                Result.success("已读") : Result.error(500, "操作失败");
    }


}
