package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工接口")
public class EmployeeController {

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
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

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
    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @ApiOperation("添加员工")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employee) {
        log.info("新增员工：{}", employee);
        employeeService.save(employee);
        return Result.success();
    }

    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);
        employeePageQueryDTO.setPage(employeePageQueryDTO.getPage() - 1);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("修改员工状态")
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id) {
        log.info("修改员工状态：状态 {}，员工id {}", status, id);
        employeeService.changeStatus(status, id);
        return Result.success();
    }

    @ApiOperation("根据id查询员工信息")
    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Integer id) {
        log.info("查询员工id: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        return Result.success(employee);
    }

    @ApiOperation("编辑员工信息")
    @PutMapping
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息: {}", employeeDTO);
        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }
}
