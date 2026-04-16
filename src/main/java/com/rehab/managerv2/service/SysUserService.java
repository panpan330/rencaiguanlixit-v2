package com.rehab.managerv2.service;

import com.rehab.managerv2.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author author
 * @since 2026-04-14
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录，返回 JWT Token
     * @param loginUser 包含用户名和密码
     * @return JWT Token
     */
    String login(SysUser loginUser);

    /**
     * 新增用户，包含密码加密逻辑
     * @param sysUser 用户信息
     * @return 是否成功
     */
    boolean saveUserWithEncryption(SysUser sysUser);
}
