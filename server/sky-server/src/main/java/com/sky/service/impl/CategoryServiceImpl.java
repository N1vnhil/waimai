package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public Category queryCategoryById(Integer id) {
        return categoryMapper.queryCategoryById(id);
    }

    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        categoryMapper.insert(category);
    }

    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = new PageResult();
        List<Category> categories = categoryMapper.pageQuery(categoryPageQueryDTO);
        pageResult.setRecords(categories);
        pageResult.setTotal(categories.size());
        return pageResult;
    }

    public void changeStatus(Integer status, Long id) {
        Category category = new Category();
        category.setStatus(status);
        category.setId(id);
        categoryMapper.update(category);
    }

    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    public List<Category> queryByType(Integer type) {
        return categoryMapper.queryByType(type);
    }

    public void delete(Integer id) {
        categoryMapper.delete(id);
    }
}
