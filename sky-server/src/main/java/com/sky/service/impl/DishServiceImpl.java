package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService{
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;




    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        Long id = dish.getId() ;
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> flavor.setDishId(id));
            dishFlavorMapper.insertBatch(flavors);
        }

    }


    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new RuntimeException(MessageConstant.DISH_ON_SALE);
            }
        }
        List<Long> setmealIds = setMealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0){
            throw new RuntimeException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            dishFlavorMapper.deleteByDishId(id);
//        }
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }


    public DishVO getByIdWithFlavor(Long id) {
        // 查询菜品数据
        Dish dish = dishMapper.getById(id);
        // 查询当前菜品对应的口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }


    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 修改菜品数据
        dishMapper.update(dish);
        // 删除当前菜品对应的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        // 添加当前菜品对应的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
