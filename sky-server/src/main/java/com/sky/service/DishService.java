package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    public void saveAndFlavor(DishDTO dishDTO);

    PageResult queryPageDish(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    DishVO getDishAndFlavorById(long id);

    void updateDishAndFlavor(DishDTO dishDTO);

    void updateStatusDish(long id, Integer status);
}
