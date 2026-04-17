package com.rehab.managerv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rehab.managerv2.common.Result;
import com.rehab.managerv2.entity.SysDictData;
import com.rehab.managerv2.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys-dict-data")
public class SysDictDataController {

    @Autowired
    private SysDictDataService sysDictDataService;

    @GetMapping("/list")
    public Result list(String dictType) {
        QueryWrapper<SysDictData> queryWrapper = new QueryWrapper<>();
        if (dictType != null && !dictType.isEmpty()) {
            queryWrapper.eq("dict_type", dictType);
        }
        queryWrapper.orderByAsc("dict_sort");
        List<SysDictData> list = sysDictDataService.list(queryWrapper);
        return Result.success(list);
    }

    @PostMapping("/save")
    public Result save(@RequestBody SysDictData sysDictData) {
        boolean success = sysDictDataService.saveOrUpdate(sysDictData);
        return success ? Result.success() : Result.error(500, "保存失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = sysDictDataService.removeById(id);
        return success ? Result.success() : Result.error(500, "删除失败");
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(sysDictDataService.getById(id));
    }
}
