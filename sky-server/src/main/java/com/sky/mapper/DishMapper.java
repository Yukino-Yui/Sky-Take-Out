package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id来查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询菜品
     * 多表查询，因为菜品绑定着分类category，还要select categoryName确定菜品属于哪个分类
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQueryDish(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Select("select * from Dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据id删除菜品
     * @param id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据菜品id集合批量删除菜品
     * 是对上面的一次sql删除一次菜品的优化
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id动态修改菜品
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dish);
}
