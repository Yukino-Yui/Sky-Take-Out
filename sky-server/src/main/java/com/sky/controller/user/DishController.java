package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id条件查询菜品和口味
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id条件查询菜品及口味")
    public Result<List<DishVO>> list(Long categoryId) {

        //1.查询redis有无缓存数据
        //构造redis中的key，规则：dish_分类id
        String key = "dish_" + categoryId;
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key); //获取缓存数据
        if(list != null && !list.isEmpty()){
            //有缓存数据就直接返回给用户，无需查询数据库
            return Result.success(list);
        }
        //2.若无缓存数据，则查询数据库
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品,停售的菜品不查询


        list = dishService.listWithFlavor(dish);

        //3.将查询到的数据放入redis缓存中
        redisTemplate.opsForValue().set(key, list);


        //4.返回结果
        return Result.success(list);
    }

}
