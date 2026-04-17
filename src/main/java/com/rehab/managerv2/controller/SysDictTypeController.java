package com.rehab.managerv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysDictType;
import com.rehab.managerv2.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys-dict-type")
public class SysDictTypeController {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @GetMapping("/list")
    public Result list(String dictName, String dictType) {
        QueryWrapper<SysDictType> queryWrapper = new QueryWrapper<>();
        if (dictName != null && !dictName.isEmpty()) {
            queryWrapper.like("dict_name", dictName);
        }
        if (dictType != null && !dictType.isEmpty()) {
            queryWrapper.like("dict_type", dictType);
        }
        queryWrapper.orderByDesc("dict_id");
        List<SysDictType> list = sysDictTypeService.list(queryWrapper);
        return Result.success(list);
    }

    @PostMapping("/save")
    public Result save(@RequestBody SysDictType sysDictType) {
        if (sysDictType.getDictId() == null) {
            // Check uniqueness of dictType
            QueryWrapper<SysDictType> checkWrapper = new QueryWrapper<>();
            checkWrapper.eq("dict_type", sysDictType.getDictType());
            if (sysDictTypeService.count(checkWrapper) > 0) {
                return Result.error(500, "字典类型已存在");
            }
        }
        boolean success = sysDictTypeService.saveOrUpdate(sysDictType);
        return success ? Result.success() : Result.error(500, "保存失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        // Ideally should check if any data relies on this type
        boolean success = sysDictTypeService.removeById(id);
        return success ? Result.success() : Result.error(500, "删除失败");
    }
    
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(sysDictTypeService.getById(id));
    }
}
