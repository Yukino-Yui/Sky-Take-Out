package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 *菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result saveDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveDishWithFlavor(dishDTO);

        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageQueryDish(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQueryDish(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     * @RequestPara注解，由spring MVC 实现，实现前端传来的String类型数据的自动转换
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result deleteDish(@RequestParam List<Long> ids) {
        log.info("删除菜品：{}", ids);
        dishService.deleteDish(ids);
        return Result.success();

    }

    /**
     * 根据id查询菜品
     * 用于数据的回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishvo = dishService.getDishById(id);
        return Result.success(dishvo);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}",dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }

}
