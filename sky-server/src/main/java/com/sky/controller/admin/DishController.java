package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
//菜品相关接口
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //新增菜品
    @PostMapping
    public Result<T> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.saveAndFlavor(dishDTO);
        String key = "dish" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
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
        Set<String> keys = redisTemplate.keys("dish*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
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
        Set<String> keys = redisTemplate.keys("dish*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result<T> updateStatusDish(@RequestParam long id, @PathVariable Integer status) {
        log.info("修改菜品起售，禁售状态:{},{}", id, status);
        dishService.updateStatusDish(id, status);
        Set<String> keys = redisTemplate.keys("dish*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
        return Result.success();
    }
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        String key = "dish" + categoryId;
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        log.info("getRedis:{}:{}", key, list);
        if (list != null && !list.isEmpty()) {
            return Result.success(list);
        }
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        list = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key, list, 60, TimeUnit.SECONDS);
        log.info("setRedis:{}:{}", key, list);
        return Result.success(list);
    }
}
