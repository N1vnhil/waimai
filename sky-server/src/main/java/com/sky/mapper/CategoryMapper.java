package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("select * from category where id=#{id}")
    Category queryCategoryById(Integer id);

    @Insert("insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values(#{id}, #{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    int insert(Category category);

    List pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    int update(Category category);

    List queryByType(Integer type);

    @Delete("delete from category where id=#{id}")
    int delete(Integer id);
}
