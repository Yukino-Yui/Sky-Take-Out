package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")//管理端发出的请求，统一使用/admin作为前缀，用户端则用/user
@Slf4j
public class EmployeeController {

    //Autowired 的作用是：
    //告诉 Spring：“请帮我把这个 Bean 注入进来，我不想自己 new
    //Spring 会扫描容器，找到实现 EmployeeService 的 Bean
    //自动赋值给 employeeService
    //你在 Controller 里就可以直接用 employeeService.login(...)
    //如果没有 @Autowired：你只是定义了一个成员变量：
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    //当前端提交过来的数据和实体类中对应的属性差别比较大时，建议使用DTO来封装数据
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        //key 是String类型，value是Object也就是可以是任何类型
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        //createJWT是正式生成令牌，工具类都是静态方法，所以通过JwtUtil调用createJWT
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        //Builder是一种设计模式，这里用来创建一个封装前端需要的EmployeeLoginVO对象
        //作用：链式调用，代码清晰，不用写一大堆 set 方法
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        //这种写法 程序运行时会把后面的参数动态的拼到{}里面
       log.info("新增员工：{}", employeeDTO);
       employeeService.save(employeeDTO);
       return Result.success();

    }
}
