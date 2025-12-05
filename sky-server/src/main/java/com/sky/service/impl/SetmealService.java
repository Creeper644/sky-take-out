package com.sky.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService {
    void deleteBatch(List<Long> ids);
}
