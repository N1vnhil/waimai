package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {

    public Category queryCategoryById(Integer id);

    public void addCategory(CategoryDTO categoryDTO);

    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    public void changeStatus(Integer status, Long id);

    public void updateCategory(CategoryDTO categoryDTO);

    public List<Category> queryByType(Integer type);

    public void delete(Integer type);
}
