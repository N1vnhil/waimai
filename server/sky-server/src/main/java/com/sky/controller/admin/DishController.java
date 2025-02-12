package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;


@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageResult(DishPageQueryDTO pageQueryDTO) {
        log.info("菜品分页查询：{}", pageQueryDTO);
        pageQueryDTO.setPage(pageQueryDTO.getPage() - 1);
        return Result.success(dishService.getPageResult(pageQueryDTO));
    }

    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result deleteById(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> queryById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        return Result.success(dishService.getByIdWithFlavor(id));
    }

    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品状态")
    public Result updateStatus(Long id, @PathVariable Integer status) {
        log.info("修改菜品状态：{}, {}", id, status);
        dishService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> queryByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        return Result.success(dishService.list(dish));
    }
}
