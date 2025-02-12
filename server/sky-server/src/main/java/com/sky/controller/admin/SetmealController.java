package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.beans.Beans;
import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @ApiOperation("新增套餐")
    @PostMapping
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getSetmealWithDish(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        SetmealVO setmealVO = setmealService.getSetmealWithDishById(id);
        if(setmealVO == null) return Result.error("Setmeal Not Found");
        return Result.success(setmealVO);
    }

    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询");
        return Result.success(setmealService.pageQuery(setmealPageQueryDTO));
    }

    @ApiOperation("套餐批量删除")
    @DeleteMapping
    public Result deleteSetmeal(@RequestParam List<Long> ids) {
        log.info("套餐批量删除：{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    @ApiOperation("修改套餐")
    @PutMapping
    public Result updateSetmealWithDish(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        setmealService.updateSetmealWithDish(setmealDTO);
        return Result.success();
    }

    @ApiOperation("修改套餐状态")
    @PostMapping("/status/{status}")
    public Result updateStatus(Long id, @PathVariable Integer status) {
        log.info("修改套餐状态：{}, {}", id, status);
        setmealService.updateStatus(id, status);
        return Result.success();
    }

}
