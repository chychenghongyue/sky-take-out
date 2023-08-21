package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
//菜品相关接口
public class DishController {
    @Autowired
    private DishService dishService;

    //新增菜品
    @PostMapping
    public Result<T> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.saveAndFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> queryPageDish(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.queryPageDish(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result<T> delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除:{}", ids);
        dishService.delete(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> getDishAndFlavorById(@PathVariable long id) {
        log.info("根据id获取菜品信息:{}", id);
        DishVO dishVO = dishService.getDishAndFlavorById(id);
        return Result.success(dishVO);
    }

    @PutMapping()
    public Result<T> updateDishAndFlavor(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息:{}", dishDTO);
        dishService.updateDishAndFlavor(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result<T> updateStatusDish(@RequestParam long id, @PathVariable Integer status) {
        log.info("修改菜品起售，禁售状态:{},{}", id, status);
        dishService.updateStatusDish(id, status);
        return Result.success();
    }
}
