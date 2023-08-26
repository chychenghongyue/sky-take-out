package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PutMapping("/{status}")
    public Result<T> setStatus(@PathVariable Integer status) {
        log.info("redisTemplate:{}", redisTemplate);
        log.info("设置店铺营业状态:{}", status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set("SHOP_STATUS", status, 2, TimeUnit.DAYS);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getStatus() {
        log.info("redisTemplate:{}", redisTemplate);
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("获取店铺营业状态:{}", status != null && status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
