package com.rehab.managerv2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rehab.managerv2.entity.BizRehabMotionData;

import java.util.List;

public interface BizRehabMotionDataService extends IService<BizRehabMotionData> {
    
    /**
     * 批量接收物联网设备数据，并异步落库或先入 Redis 缓存
     */
    void batchReceiveMotionData(List<BizRehabMotionData> dataList);
}
