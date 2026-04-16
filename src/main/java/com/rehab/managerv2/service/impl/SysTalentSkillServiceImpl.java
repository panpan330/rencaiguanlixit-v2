package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.entity.SysTalentSkill;
import com.rehab.managerv2.mapper.SysTalentSkillMapper;
import com.rehab.managerv2.service.SysTalentSkillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 交叉人才技能雷达分数表 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-04-16
 */
@Service
public class SysTalentSkillServiceImpl extends ServiceImpl<SysTalentSkillMapper, SysTalentSkill> implements SysTalentSkillService {
    @Override
    public SysTalentSkill getSkillByTalentId(Long talentId) {
        // 所有的数据库查询条件拼装，严禁离开 Service 层！
        QueryWrapper<SysTalentSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("talent_id", talentId);
        return this.getOne(queryWrapper);
    }


}
