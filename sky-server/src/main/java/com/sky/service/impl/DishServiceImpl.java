package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Transactional //事务注解，用于处理数据库事务
    public void saveDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();

        //对象属性拷贝
        BeanUtils.copyProperties(dishDTO, dish);

        //设置公共字段的值（AOP）

        //调用mapper插入一条记录到dish表
        dishMapper.insert(dish);

        //获取Dishmapper Insert语句生成的主键值，通过insert语句插入生成dishId后并返回后，才能获取dishi
        Long id =  dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            //dishFalvor是形参名，代指当前循环中的DishFlavor对象
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            //插入n条记录到dish_flavor表（可能有多个口味，可能也没有口味）
            dishFlavorMapper.insertBatch(flavors);
        }

    }

}
