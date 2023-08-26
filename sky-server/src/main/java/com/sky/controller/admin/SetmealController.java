package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("id:{}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @CacheEvict(cacheNames = "setmeal")
    public Result<T> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("setmealDTO:{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }


    @PostMapping
    @CacheEvict(cacheNames = "setmeal")
    public Result<T> save(@RequestBody SetmealDTO setmealDTO) {
        log.info("setmealDTO:{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @CacheEvict(cacheNames = "setmeal")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("setmealPageQueryDTO:{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @CacheEvict(cacheNames = "setmeal")
    public Result<T> delete(@RequestParam List<Long> ids) {
        log.info("ids:{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @CachePut(cacheNames = "setmeal", key = "#id")
    public Result<T> startOrStop(@PathVariable Integer status, Long id) {
        log.info("id:{},status:{}", id, status);
        setmealService.startOrStop(status, id);
        return Result.success();
    }
}
