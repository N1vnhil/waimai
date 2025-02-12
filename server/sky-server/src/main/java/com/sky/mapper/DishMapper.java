package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DishMapper {

    @AutoFill(value = OperationType.INSERT)
    public int insert(Dish dish);

    public List<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    @Delete("delete from dish where id=#{id}")
    int deleteById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    int update(Dish dish);

    List<Dish> query(Dish dish);

}
