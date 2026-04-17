package com.rehab.managerv2.controller;

import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysTalentProfile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeParserController {

    @PostMapping("/parse")
    public Result<Map<String, Object>> parseResume(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "请上传简历文件");
        }
        
        try (InputStream is = file.getInputStream(); PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            Map<String, Object> result = new HashMap<>();
            SysTalentProfile profile = new SysTalentProfile();
            
            // 简单的规则提取示例 (可扩展)
            if (text.contains("男")) {
                result.put("gender", "男");
            } else if (text.contains("女")) {
                result.put("gender", "女");
            }

            if (text.contains("博士")) {
                profile.setEducationLevel("博士");
            } else if (text.contains("硕士")) {
                profile.setEducationLevel("硕士");
            } else if (text.contains("本科")) {
                profile.setEducationLevel("本科");
            }

            // 提取手机号正则
            java.util.regex.Pattern phonePattern = java.util.regex.Pattern.compile("1[3-9]\\d{9}");
            java.util.regex.Matcher phoneMatcher = phonePattern.matcher(text);
            if (phoneMatcher.find()) {
                profile.setPhone(phoneMatcher.group());
            }
            
            // 简单截取研究方向
            if (text.contains("研究方向")) {
                int start = text.indexOf("研究方向") + 4;
                int end = text.indexOf("\n", start);
                if (end > start) {
                    profile.setResearchDirection(text.substring(start, end).replaceAll("[:：]", "").trim());
                }
            }

            result.put("profile", profile);
            result.put("rawText", text); // 返回原文以供后续处理
            return Result.success(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "解析简历失败: " + e.getMessage());
        }
    }
}
