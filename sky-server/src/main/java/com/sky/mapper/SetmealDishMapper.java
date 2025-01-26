package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> querySetmealIdsByDishIds(List<Long> dishIds);

    int insertBatch(List<SetmealDish> setmealDishes);

    @Select("select * from setmeal_dish where setmeal_id=#{setmealId}")
    List<SetmealDish> querySetmealDishesBySetmealId(Long setmealId);

    @Delete("delete from setmeal_dish where setmeal_id=#{setmealId}")
    int deleteBySetmealId(Long setmealId);

}
