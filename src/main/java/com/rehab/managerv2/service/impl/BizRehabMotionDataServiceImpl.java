package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rehab.managerv2.entity.BizRehabMotionData;
import com.rehab.managerv2.mapper.BizRehabMotionDataMapper;
import com.rehab.managerv2.service.BizRehabMotionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BizRehabMotionDataServiceImpl extends ServiceImpl<BizRehabMotionDataMapper, BizRehabMotionData> implements BizRehabMotionDataService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String IOT_DATA_QUEUE = "iot:motion_data:queue";

    @PostConstruct
    public void initMockData() {
        try {
            // 如果数据库里一条康复数据都没有，系统启动时自动造一些逼真的康复运动数据进去
            if (this.count() == 0) {
                List<BizRehabMotionData> list = new ArrayList<>();
                long now = System.currentTimeMillis();
                for (int i = 0; i < 150; i++) {
                    BizRehabMotionData data = new BizRehabMotionData();
                    data.setTalentId(1L); // 绑定给人才1（假设是我们的测试对象）
                    data.setDeviceId("IOT-GLOVE-001");
                    data.setMotionType("手指屈伸综合训练");
                    
                    // 模拟一个逐渐康复的螺旋上升轨迹：动作越来越大，力量越来越强
                    double t = i * 0.1;
                    data.setAngleX(BigDecimal.valueOf(50 + 30 * Math.sin(t))); // X轴角度
                    data.setAngleY(BigDecimal.valueOf(50 + 30 * Math.cos(t))); // Y轴角度
                    data.setAngleZ(BigDecimal.valueOf(0)); // Z轴这里不用
                    data.setForceValue(BigDecimal.valueOf(10 + i * 0.25));     // 受力值逐渐增大
                    
                    // 时间往前推算，模拟连续的训练记录
                    data.setRecordTime(new Date(now - (150 - i) * 1000L));
                    data.setCreateTime(new Date());
                    list.add(data);
                }
                this.saveBatch(list);
                System.out.println("====== 真实物联网康复数据已成功初始化！ ======");
            }
        } catch (Exception e) {
            // 避免 NullPointerException 被吃掉
            System.err.println("初始化物联网康复数据失败，可能是 Redis 或 MySQL 未启动：");
            e.printStackTrace();
        }
    }

    @Override
    public void batchReceiveMotionData(List<BizRehabMotionData> dataList) {
        // 物联网设备高频上报，先推入 Redis 队列进行缓冲，削峰填谷
        for (BizRehabMotionData data : dataList) {
            redisTemplate.opsForList().rightPush(IOT_DATA_QUEUE, data);
        }
        System.out.println("收到 " + dataList.size() + " 条物联网数据，已推入 Redis 缓冲队列！");
    }

    /**
     * 定时任务：每 5 秒从 Redis 队列中批量拉取数据并落入 MySQL
     */
    @Scheduled(fixedRate = 5000)
    public void consumeMotionDataFromRedis() {
        Long size = redisTemplate.opsForList().size(IOT_DATA_QUEUE);
        if (size != null && size > 0) {
            List<BizRehabMotionData> batchList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            // 每次最多处理 500 条
            int limit = Math.min(size.intValue(), 500);
            for (int i = 0; i < limit; i++) {
                Object rawData = redisTemplate.opsForList().leftPop(IOT_DATA_QUEUE);
                if (rawData != null) {
                    try {
                        // 防止存入 Redis 的是 LinkedHashMap 等序列化对象，先转成 JSON 字符串再反序列化回对象
                        BizRehabMotionData data = objectMapper.convertValue(rawData, BizRehabMotionData.class);
                        batchList.add(data);
                    } catch (Exception e) {
                        System.err.println("从 Redis 反序列化运动数据失败: " + e.getMessage());
                    }
                }
            }
            
            if (!batchList.isEmpty()) {
                this.saveBatch(batchList);
                System.out.println("====== 定时任务触发：从 Redis 取出 " + batchList.size() + " 条物联网数据并成功落入 MySQL！ ======");
            }
        }
    }
}
