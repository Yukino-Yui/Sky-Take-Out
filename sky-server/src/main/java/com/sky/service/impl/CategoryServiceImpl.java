package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.dto.CategoryDTO;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setmealMapper;

    /**
     * 添加分类
     * @param categoryDTO
     */
    public void addCategory(CategoryDTO categoryDTO){
        //增加到数据库里面的是实体，传过来的DTO字段不够 所以需要new一个Category对象
        Category category = new Category();
        //进行属性的拷贝
        BeanUtils.copyProperties(categoryDTO,category);
        //设置状态 0：禁用 1：启用
        category.setStatus(StatusConstant.ENABLE);

//        //设置创建时间和最后修改时间
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//
//        //创建人id和最后修改人id
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.addCategory(category);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    public void updateCategory(CategoryDTO categoryDTO){
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

//        //创建一个category对象来接收categoryDTO，因为还要更改更新时间和当前执行更新操作的用户
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.updateCategory(category);

    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     *///分页查询这里的代码理解的不太好，晚上回来好好看一下
    public PageResult pageQueryCategory(CategoryPageQueryDTO categoryPageQueryDTO){

        //开启分页startPage 必须紧邻下一句的mapper查询，也就是说这个startpage会把参数页码和页数动态插入到sql语句
        //例如参数是2，10(第二页，每页十条) select * from category limit 10,10(执行 SQL 之前，自动往 SQL 里加 LIMIT。)
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());

        //查询，返回值必须是 Page<T>，PageHelper在执行SQL时，会动态把 mapper 返回值改成 Page 类型
        Page<Category> page = categoryMapper.pageQueryCategory(categoryPageQueryDTO);

        //PageHelper把查询结果包装成一个Page对象，里面的方法，page.getTotal返回总条数
        //page.getResult返回当前页的数据列表，每一个记录都是Category类型
        Long total = page.getTotal();
        List<Category> records = page.getResult();
        return new PageResult(total,records);
    }

    /**
     * 启用或禁用分类
     * @param status
     * @param id
     */
    public void startOrStopCategory(Integer status, Long id){
        //这里要动态更新 ，所以传这两个参数到mapper里面的方法并不合适，比如还得修改时间等
        //所以要用动态更新的方法，用Category实体类来封装

        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();
        //调用update，即降低了耦合，又能满足更新时间和updateUser，同时不用再写一遍update的sql语句

//        //不要忘了更新updateTime和updateUser
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.updateCategory(category);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    public void deleteCategory(Long id){
        // 查询当前分类是否关联了菜品，如果关联了就抛出业务异常,因为菜品里面category_id是外键
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常，因为套餐里面category_id是外键
        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除分类数据
        categoryMapper.deleteCategory(id);

    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    public List<Category> listCategory(Integer type){
        return categoryMapper.listCategory(type);
    }

}
