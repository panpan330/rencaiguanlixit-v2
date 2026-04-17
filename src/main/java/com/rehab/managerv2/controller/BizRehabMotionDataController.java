package com.rehab.managerv2.controller;

import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.BizRehabMotionData;
import com.rehab.managerv2.service.BizRehabMotionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物联网康复设备接口
 */
@RestController
@RequestMapping("/api/iot/motion-data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BizRehabMotionDataController {

    @Autowired
    private BizRehabMotionDataService motionDataService;

    /**
     * 接收设备实时上报的运动数据
     * 支持批量上报，并使用 Redis 队列缓冲
     */
    @PostMapping("/upload")
    public Result<?> uploadData(@RequestBody List<BizRehabMotionData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return Result.error(400, "上报数据不能为空");
        }
        motionDataService.batchReceiveMotionData(dataList);
        return Result.success("数据接收成功，已加入处理队列");
    }

    /**
     * 查询某人才(患者)的运动康复数据轨迹（前端 3D 可视化接口）
     */
    @GetMapping("/list/{talentId}")
    public Result<List<BizRehabMotionData>> getMotionDataByTalent(@PathVariable Long talentId) {
        // 此处可通过 Redis 缓存高频查询数据，为了演示直接查库
        List<BizRehabMotionData> list = motionDataService.lambdaQuery()
                .eq(BizRehabMotionData::getTalentId, talentId)
                .orderByAsc(BizRehabMotionData::getRecordTime)
                .last("limit 1000") // 限制条数防爆内存
                .list();
        return Result.success(list);
    }
}
