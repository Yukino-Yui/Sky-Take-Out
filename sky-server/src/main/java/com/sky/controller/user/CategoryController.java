package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "C端-分类接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据分类类型查询分类(用于显示所有的菜品分类和套餐分类)
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询分类")
    //1：菜品分类 2：套餐分类
    public Result<List<Category>> list(Integer type) {
        List<Category> list = categoryService.listCategory(type);
        return Result.success(list);
    }
}