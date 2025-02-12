package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import jdk.net.SocketFlow;
import lombok.extern.slf4j.Slf4j;
import org.openxmlformats.schemas.drawingml.x2006.main.impl.CTGeomRectImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Beans;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Transactional
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors != null && dishFlavors.size() > 0) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    public PageResult getPageResult(DishPageQueryDTO dishPageQueryDTO) {
        List<DishVO> dishVOS = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(dishVOS.size(), dishVOS);
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断是否存在起售中的菜品
        for(Long id: ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 判断菜品是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.querySetmealIdsByDishIds(ids);
        if(!setmealIds.isEmpty()) throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

        // 删除菜品
        for(Long id: ids) dishMapper.deleteById(id);

        // 删除关联口味
        for(Long id: ids) dishFlavorMapper.deleteByDishId(id);
    }

    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavorMapper.queryFlavorById(id));
        return dishVO;
    }

    public void updateWithFlavor(DishDTO dishDTO) {
        // 修改dish表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        // 删除原有flavor信息
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        // 添加新flavor信息
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors != null && dishFlavors.size() > 0) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    public void updateStatus(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }

    public List<DishVO> listWithFlavor(Dish q) {
        List<Dish> dishes = dishMapper.query(q);
        List<DishVO> dishVOS = new ArrayList<>();
        for(Dish dish: dishes) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setFlavors(dishFlavorMapper.queryFlavorById(dish.getId()));
            dishVOS.add(dishVO);
        }
        return dishVOS;
    }

    public List<Dish> list(Dish q) {
        return dishMapper.query(q);
    }
}
