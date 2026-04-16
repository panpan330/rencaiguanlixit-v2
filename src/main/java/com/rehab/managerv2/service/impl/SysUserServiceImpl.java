package com.rehab.managerv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.common.JwtUtils;
import com.rehab.managerv2.entity.SysUser;
import com.rehab.managerv2.mapper.SysUserMapper;
import com.rehab.managerv2.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

        // 3. 一切无误，发放 JWT 通行证
        return JwtUtils.generateToken(dbUser.getUsername());
    }

    @Override
    public boolean saveUserWithEncryption(SysUser sysUser) {
        // 1. 业务规则处理：默认密码
        String rawPassword = sysUser.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            rawPassword = "123";
        }

        // 2. 数据安全处理：BCrypt 强加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        sysUser.setPassword(passwordEncoder.encode(rawPassword));

        // 3. 调用父类 IService 的 save 方法入库
        return this.save(sysUser);
    }

}
