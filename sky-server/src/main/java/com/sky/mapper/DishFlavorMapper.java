package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除关联口味数据
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id集合批量删除关联口味数据
     * 是对上面一次sql删除一个口味的优化
     * @param DishIds
     */
    void deleteBatch(List<Long> DishIds);

    /**
     * 根据菜品id查询关联的口味数据
     * @param dishId
     * @return
     */
    List<DishFlavor> getByDishId(Long dishId);

}
