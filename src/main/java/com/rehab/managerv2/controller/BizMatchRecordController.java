package com.rehab.managerv2.controller;


import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.BizMatchRecord;
import com.rehab.managerv2.service.BizMatchRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
@RestController
@RequestMapping("/biz-match-record")
public class BizMatchRecordController {

    @Autowired
    private BizMatchRecordService matchRecordService;

    /**
     * 触发智能撮合算法
     * @param projectId 项目ID
     * @return 撮合结果集
     */
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

}
