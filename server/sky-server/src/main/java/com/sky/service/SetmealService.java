package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    void addSetmeal(SetmealDTO setmealDTO);

    SetmealVO getSetmealWithDishById(Long id);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteBatch(List<Long> ids);

    void updateSetmealWithDish(SetmealDTO setmealDTO);

    void updateStatus(Long id, Integer status);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishesBySetmealId(Long id);
}
