package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     * @param employee
     */
    @Insert("insert into employee(name, username, password, phone, sex,id_number,create_time,update_time,create_user,update_user,status)" +
    "values (#{name},#{username},#{password}, #{phone}, #{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    void insert(Employee employee);

    /**
     * 根据姓名分页查询员工数据
     * @param employeePageQueryDTO
     * @return
     */
     Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键id动态修改员工状态or修改员工信息
     * @param employee
     */// 注意这里接收的类型是employee，所以在sql操作里面必须对employee里面有的属性进行操作
       // 比如说传过来的如果是employeeDto，里面没有password，createTime等字段，如果在xml文件写这些就没法通过mybatis
        // 的getter方法查到，进一步没法执行sql语句
     void update(Employee employee);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);


}
