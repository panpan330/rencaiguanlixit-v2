package com.rehab.managerv2.controller;

import com.rehab.managerv2.entity.SysTalentProfile;
import com.rehab.managerv2.service.SysTalentProfileService;
import com.rehab.managerv2.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse; // 如果你是 SpringBoot2 则是 javax.servlet...
import java.io.IOException;
import java.net.URLEncoder;

import java.util.List;

@RestController
@RequestMapping("/sys-talent-profile")
public class SysTalentProfileController {

    @Autowired
    private SysTalentProfileService sysTalentProfileService;

    /**
     * 获取所有人才档案列表
     */
    @GetMapping("/list")
    public Result getList() {
        List<SysTalentProfile> list = sysTalentProfileService.list();
        return Result.success(list);
    }

    /**
     * 保存或更新人才档案
     */
    @PostMapping("/save")
    public Result save(@RequestBody SysTalentProfile sysTalentProfile) {
        boolean success = sysTalentProfileService.saveOrUpdate(sysTalentProfile);
        if (success) {
            return Result.success("保存成功");
        }
        // 🔥 修改点：加上了 500 错误码，完美匹配你的 Result 类！
        return Result.error(500, "保存失败");
    }

    /**
     * 删除人才档案
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = sysTalentProfileService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        // 🔥 修改点：加上了 500 错误码！
        return Result.error(500, "删除失败");
    }
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 1. 设置响应头，告诉浏览器我们要下载的是 Excel 文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 2. 设置下载的文件名
        String fileName = URLEncoder.encode("交叉人才档案数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 3. 查询数据库里所有的数据
        List<SysTalentProfile> list = sysTalentProfileService.list();

        // 4. 使用 EasyExcel 直接把 list 塞进响应流里，完成下载！
        EasyExcel.write(response.getOutputStream(), SysTalentProfile.class)
                .sheet("人才数据")
                .doWrite(list);
    }
}