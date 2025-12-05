package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import org.springframework.stereotype.Service;

@Service
public interface DishService {
    public void saveWhitFlavor(DishDTO dishDTO);
}
