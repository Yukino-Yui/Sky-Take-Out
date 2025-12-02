package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public void add(ShoppingCartDTO shoppingCartDTO){
        //判断当前加入到购物车中的商品是否已经存在了
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId(); //获取用户id，不同用户都有自己的购物车
        shoppingCart.setUserId(userId);
        //动态条件查询
        //select * from shopping_cart where userid = ? and setmeal_id = ?
        //select * from shopping_cart where userid = ? and dish_id = ? and dish_flavor = ?
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //如果已经存在了，只需要将数量加一，更新number即可
        if(list != null && !list.isEmpty()){
            // 这里为什么是get(0),其实很好理解，每次进行一次查询，是动态查询的，如果DTO里面是套餐id，userId
            // 查询得到的list里面的ShoppingCart一定是套餐，一个setmeal_id 和 userId只可能对应一个数据
            // 用list接收是为了保证规范，查询返回的都是list对象集合，只不过这里结合了userid，所以集合里面只有一条数据
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);//update shopping_cart set number = ? where id = ?
            shoppingCartMapper.updateNumberById(cart);
        }
        //如果不存在
        else{
            //判断本次添加到购物车的是菜品还是套餐
            if(shoppingCart.getSetmealId() != null){
                //本次添加进购物车的是套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());

            }
            else{
                //本次添加进购物车的是菜品
                Dish dish = dishMapper.getById(shoppingCart.getDishId());
                //因为要插入一个shoppingCart数据，所以name amount image字段必须获取到，然后赋给shoppingCart对象里面对应的字段
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());

                if(shoppingCart.getDishFlavor() != null){

                }
            }
            //二者重复字段放外面
            shoppingCart.setNumber(1); //初次数量设为1
            shoppingCart.setCreateTime(LocalDateTime.now());//设置当前创建时间
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId(); //获取到当前微信用户的id
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .id(userId)
                .build();
        List<ShoppingCart> list =  shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    public void cleanShoppingCart(){
        Long userId = BaseContext.getCurrentId(); //获取到当前微信用户的id
        shoppingCartMapper.deleteByUserId(userId);
    }
}
