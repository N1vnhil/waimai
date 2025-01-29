package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.awt.peer.ListPeer;
import java.util.List;

@Mapper
public interface SetmealMapper {

    @AutoFill(value = OperationType.INSERT)
    int insert(Setmeal setmeal);

    @Select("select * from setmeal where id=#{id}")
    Setmeal queryById(Long id);

    List<Setmeal> page(SetmealPageQueryDTO setmealPageQueryDTO);

    @Delete("delete from setmeal where id=#{id}")
    int deleteById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    int update(Setmeal setmeal);

    List<Setmeal> query(Setmeal setmeal);
}
