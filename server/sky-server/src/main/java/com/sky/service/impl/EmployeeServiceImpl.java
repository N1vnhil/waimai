package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 对象属性拷贝 BeanUtils.copyProperties(src, tgt) 要求属性名一致
        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置员工状态
        employee.setStatus(StatusConstant.ENABLE);

        // 设置员工默认密码 md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employeeMapper.insert(employee);

    }

    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        List<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.size());
        pageResult.setRecords(page);
        return pageResult;
    }

    public void changeStatus(Integer status, Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(status);
        employeeMapper.update(employee);
    }

    public Employee getEmployeeById(Integer id) {
        return employeeMapper.getEmployeeById(id);
    }

    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }

}
