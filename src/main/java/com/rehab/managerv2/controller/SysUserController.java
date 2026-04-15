package com.rehab.managerv2.controller;

import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysUser;
import com.rehab.managerv2.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.rehab.managerv2.common.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

@RestController
@RequestMapping("/sys-user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 登录接口：验证账号密码，颁发 JWT
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody SysUser loginUser) {
        // 1. 根据前端传过来的用户名，去数据库里查这个人
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginUser.getUsername());
        SysUser dbUser = sysUserService.getOne(queryWrapper);

        // 2. 判断账号存不存在
        if (dbUser == null) {
            return Result.error(400, "账号不存在！");
        }

        // 3. 核对密码（重点：用 BCrypt 加密器来比对明文和数据库里的密文）
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // matches(前端传来的明文密码, 数据库里的密文密码)
        boolean isMatch = passwordEncoder.matches(loginUser.getPassword(), dbUser.getPassword());

        if (!isMatch) {
            return Result.error(400, "密码错误！");
        }

        // 4. 账号密码都对，调用咱们的“印钞机”生成通行证！
        String token = JwtUtils.generateToken(dbUser.getUsername());

        // 5. 把通行证发给前端
        return Result.success(token);
    }

    @GetMapping("/list")
    public Result<Page<SysUser>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 1. 构造分页参数对象
        Page<SysUser> pageParam = new Page<>(pageNo, pageSize);

        // 2. 调用 MyBatis-Plus 的分页查询方法
        Page<SysUser> resultPage = sysUserService.page(pageParam);

        // 3. 把查出来的分页结果包装进咱们的大厂包装盒里返回
        return Result.success(resultPage);
    }

    /**
     * 新增用户 (带密码自动加密功能)
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysUser sysUser) {
        // 1. 获取前端传来的明文密码
        String rawPassword = sysUser.getPassword();

        // 如果前端没传密码，我们可以给个默认密码，比如 123
        if (rawPassword == null || rawPassword.isEmpty()) {
            rawPassword = "123";
        }

        // 2. 召唤咱们的 BCrypt 加密器
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 3. 把明文密码变成防篡改的密文
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 4. 把密文重新塞回 user 对象里
        sysUser.setPassword(encodedPassword);

        // 5. 调用 MyBatis-Plus 保存进数据库
        boolean flag = sysUserService.save(sysUser);

        return flag ? Result.success(true) : Result.error(500, "新增失败");
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SysUser sysUser) {
        boolean flag = sysUserService.updateById(sysUser);
        return flag ? Result.success(true) : Result.error(500, "修改失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean flag = sysUserService.removeById(id);
        return flag ? Result.success(true) : Result.error(500, "删除失败");
    }
}