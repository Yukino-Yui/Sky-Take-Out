package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishService dishService;

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

        // 获取Dishmapper Insert语句生成的主键值（因为设计上就是主键自增），
        // insert 执行成功后：MyBatis 会自动把数据库生成的自增主键赋值到dish.id 上

        Long id =  dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors != null && !flavors.isEmpty()){
            //dishFalvor是形参名，代指当前循环中的DishFlavor对象，为什么要用循环？
            //就是说口味可能有多种，比如说辣度 甜度 等，一种对应一个对象，要把每个对象的dish_id都设为id
            //也就是把口味和菜品通过口味表里面的外键dish_id把二者建立关联
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            //插入n条记录到dish_flavor表（可能有多个口味，可能也没有口味）
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     * @return
     */
    //分页查询菜品，前端请求封装为DTO，接受最后返回PageResult类型
    public PageResult pageQueryDish(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页，会把limit动态插入select sql语句当中 页码，每页记录数
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        //为什么要用DishVO，因为返回给前端的是Result.success(PageResult)，对象类型是PageResult
        //里面的records是一个列表，把列表里前端需要的每一项数据封装为一个DishVO对象，最后赋给List<DishVO>records
        Page<DishVO> page = dishMapper.pageQueryDish(dishPageQueryDTO);

        Long total = page.getTotal();

        List<DishVO> records = page.getResult();

        //由total，records new一个PageResult对象，并返回
        return new PageResult(total,records);
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Transactional //事务注解，用于保证数据库事务一致性
    public void deleteDish(List<Long> ids) {
        //1.判断菜品的状态，是否在起售，只要有一个在起售，那么这次删除请求就不能进行
        //根据传过来的菜品id来查Dish表，返回Dish对象，getStatus来得到status
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            int status = dish.getStatus();
            if(status == StatusConstant.ENABLE){
                //当前菜品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //2.判断菜品是否关联了某个套餐，若关联则不能删
        //根据dishid 查询setmeal_dish表，看是否能查询到setmealId
        //直接传入id集合，然后用批量查询，where id in (?,?,?),返回一个id集合，通过判断集合是否为空来确定查询结果
        List<Long> setmealId = setmealDishMapper.getSetmealByDishId(ids);

        //若集合不为空，且size > 0,说明查询到了setmealId
        if(setmealId != null && !setmealId.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        /*
        //3.删除对应的菜品
        for(Long id : ids){
            dishMapper.deleteById(id);
        }

        //4.删除菜品对应的口味
        for(Long id : ids){
            dishFlavorMapper.deleteByDishId(id);
        }
        */

        //上面的for循环会导致很多的sql操作，会降低性能，每一次sql都会花费较多的时间，所以进行下面的优化
        //根据菜品id集合批量删除菜品
        //delete from dish where id in(?,?,?,?)
        dishMapper.deleteBatch(ids);

        //根据菜品id集合批量删除菜品关联的口味
        //delete from dish_flavor where dish_id in(?,?,?,?)
        dishFlavorMapper.deleteBatch(ids);

    }

    /**
     * 根据id查询菜品和关联的口味数据
     * @param id
     * @return
     */
    @Transactional
    public DishVO getDishById(Long id) {
        //先查询菜品
        Dish dish = dishMapper.getById(id);
        Long dishId = dish.getId();

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);

        //根据dish_id批量查口味数据，得到结果封装到flavors
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dishId);

        //将查询得到的口味数据赋给dishVO里面的flavors字段
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    /**
     * 修改菜品或同时修改相关联的口味
     * @param dishDTO
     */
    @Transactional
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //修改菜品
        dishMapper.updateDish(dish);
        //获取dishId
        Long dishId = dish.getId();

        //修改菜品关联的口味(这种场景一般都是通过 DELETE + INSERT 批量插入)
        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishId);

        //重新插入要修改的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //DishDTO里面的flavor数据
        if(flavors != null && !flavors.isEmpty()){

            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //批量插入n条记录到dish_flavor表（可能有多个口味，可能也没有口味）
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
