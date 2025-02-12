package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import java.util.List;

@Mapper
public interface DishFlavorMapper {

    public int insertBatch(List<DishFlavor> flavors);

    @Select("select * from dish_flavor where dish_id=#{dishId}")
    public List<DishFlavor> queryFlavorById(Long dishId);

    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    int deleteByDishId(Long dishId);
}
