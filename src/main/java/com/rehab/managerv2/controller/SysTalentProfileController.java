package com.rehab.managerv2.controller;

import com.rehab.managerv2.entity.SysTalentProfile;
// 注意：这里最好统一使用 ISysTalentProfileService 接口
import com.rehab.managerv2.service.SysTalentProfileService;
import com.rehab.managerv2.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys-talent-profile")
public class SysTalentProfileController {

    @Autowired
    private SysTalentProfileService sysTalentProfileService;

    @GetMapping("/list")
    public Result getList() {
        return Result.success(sysTalentProfileService.list());
    }

    @PostMapping("/save")
    public Result save(@RequestBody SysTalentProfile sysTalentProfile) {
        // 🔥 优化：使用三元运算符，告别啰嗦的 if-else
        return sysTalentProfileService.saveOrUpdate(sysTalentProfile) ?
                Result.success("保存成功") : Result.error(500, "保存失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return sysTalentProfileService.removeById(id) ?
                Result.success("删除成功") : Result.error(500, "删除失败");
    }

    /**
     * 🔥 重构后的导出接口：彻底解耦
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 1. 服务员 (Controller) 只负责他该干的事：处理 HTTP 协议和响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("交叉人才档案数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 2. 把输出流 (OutputStream) 扔给大厨 (Service)，让他去把数据写进流里！
        sysTalentProfileService.exportDataToStream(response.getOutputStream());
    }

    @PostMapping("/smart-match")
    public Result smartMatch(@RequestBody Map<String, Integer> weights) {
        return Result.success(sysTalentProfileService.calculateSmartMatch(weights));
    }
}