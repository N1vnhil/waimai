package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("新增分类")
    @PostMapping
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类: {}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    @ApiOperation("查询分类")
    @GetMapping("/{id}")
    public Result<Category> queryCategoryById(@PathVariable Integer id) {
        log.info("查询分类，id: {}", id);
        Category category = categoryService.queryCategoryById(id);
        return Result.success(category);
    }

    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询: {}", categoryPageQueryDTO);
        categoryPageQueryDTO.setPage(categoryPageQueryDTO.getPage() - 1);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("修改分类状态")
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id) {
        log.info("修改分类状态：员工id {}, 状态 {}", id, status);
        categoryService.changeStatus(status, id);
        return Result.success();
    }

    @ApiOperation("修改分类")
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    @ApiOperation("根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> queryByType(Integer type) {
        log.info("根据类型查询分类: {}", type);
        List<Category> categories = categoryService.queryByType(type);
        return Result.success(categories);
    }

    @ApiOperation("删除分类")
    @DeleteMapping
    public Result delete(Integer id) {
        log.info("删除分类: {}", id);
        categoryService.delete(id);
        return Result.success();
    }
}
