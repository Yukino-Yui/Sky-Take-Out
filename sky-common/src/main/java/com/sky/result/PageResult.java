package com.sky.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data//自动生成get，set方法
@AllArgsConstructor//会自动生成有参构造
@NoArgsConstructor//会自动生成无参构造
public class PageResult implements Serializable {

    private long total; //总记录数

    private List records; //当前页数据集合，里面的每一项封装的是要返回展示的对象

}
