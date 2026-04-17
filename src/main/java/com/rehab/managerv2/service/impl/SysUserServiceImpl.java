package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.common.JwtUtils;
import com.rehab.managerv2.entity.SysUser;
import com.rehab.managerv2.mapper.SysUserMapper;
import com.rehab.managerv2.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-04-14
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    @Override
    public String login(SysUser loginUser) {
        // 保留旧方法以兼容其他可能调用的地方
        Map<String, Object> result = loginAndGetInfo(loginUser);
        return result == null ? null : (String) result.get("token");
    }

    @Override
    public Map<String, Object> loginAndGetInfo(SysUser loginUser) {
        // 1. 查数据库找人
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginUser.getUsername());
        SysUser dbUser = this.getOne(queryWrapper);

        // 如果没找到人，直接返回 null
        if (dbUser == null) {
            return null;
        }

        // 2. 召唤 BCrypt 密码校验器
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(loginUser.getPassword(), dbUser.getPassword())) {
            return null; // 密码错误，也返回 null
        }

        // 3. 一切无误，发放 JWT 通行证并组装用户信息
        String token = JwtUtils.generateToken(dbUser.getUsername());
        
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("username", dbUser.getUsername());
        resultMap.put("realName", dbUser.getRealName());
        resultMap.put("userId", dbUser.getId());
        
        // 解析 role_type: 0-admin, 1-project, 2-expert, 3-student
        String roleStr = "user";
        if (dbUser.getRoleType() != null) {
            if (dbUser.getRoleType() == 0) {
                roleStr = "admin";
            } else if (dbUser.getRoleType() == 1) {
                roleStr = "project";
            } else if (dbUser.getRoleType() == 2) {
                roleStr = "expert";
            } else if (dbUser.getRoleType() == 3) {
                roleStr = "student";
            }
        }
        resultMap.put("role", roleStr);
        
        // 如果是学生角色，我们需要顺便把他的 talentId 查出来，方便前端学生端页面查询
        if ("student".equals(roleStr)) {
            com.rehab.managerv2.service.SysTalentProfileService profileService = 
                com.rehab.managerv2.common.SpringContextUtils.getBean(com.rehab.managerv2.service.SysTalentProfileService.class);
            if (profileService != null && dbUser.getPhone() != null) {
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.rehab.managerv2.entity.SysTalentProfile> pw = 
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                pw.eq(com.rehab.managerv2.entity.SysTalentProfile::getPhone, dbUser.getPhone());
                com.rehab.managerv2.entity.SysTalentProfile profile = profileService.getOne(pw, false);
                if (profile != null) {
                    resultMap.put("talentId", profile.getId());
                }
            }
        }
        
        return resultMap;
    }

    @Override
    public boolean saveUserWithEncryption(SysUser sysUser) {
        // 1. 业务规则处理：默认密码
        String rawPassword = sysUser.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            rawPassword = "123456";
        }

        // 2. 数据安全处理：BCrypt 强加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        sysUser.setPassword(passwordEncoder.encode(rawPassword));

        // 3. 调用父类 IService 的 saveOrUpdate 方法入库 (支持新增和重置密码更新)
        return this.saveOrUpdate(sysUser);
    }

}
