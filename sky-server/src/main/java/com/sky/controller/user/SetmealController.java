package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "C端-套餐浏览接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 动态条件查询
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id条件查询套餐")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId") //key:setmealCache::(动态算出来的Id)
    public Result<List<Setmeal>> list(Long categoryId) {

        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);//查询起售中的套餐，停售的套餐不查询

        List<Setmeal>list = setmealService.list(setmeal);
        return Result.success(list);
    }

    /**
     * 根据套餐id查询包含的菜品列表
     * DishItemVO封装了菜品名称，份数，菜品图片，菜品描述这四个属性
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品列表")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long id) {
        //1.先查询Redis，看是否有对应的缓存数据，没有在查询数据库
        String key = "dishWithSetmeal_" + id;
        List<DishItemVO> list = (List<DishItemVO>) redisTemplate.opsForValue().get(key);
        if(list != null && !list.isEmpty()){
            return Result.success(list);
        }

        list = setmealService.getDishItemById(id);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }
}
