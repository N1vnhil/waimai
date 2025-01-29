package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.aspectj.weaver.Lint;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Transactional
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    public SetmealVO getSetmealWithDishById(Long id) {
        Setmeal setmeal = setmealMapper.queryById(id);
        SetmealVO setmealVO = new SetmealVO();
        if(setmeal == null) return null;
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishMapper
                .querySetmealDishesBySetmealId(setmeal.getId()));
        return setmealVO;
    }

    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        setmealPageQueryDTO.setPage(setmealPageQueryDTO.getPage() - 1);
        List<Setmeal> setmeals = setmealMapper.page(setmealPageQueryDTO);
        return new PageResult(setmeals.size(), setmeals);
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        for(Long id: ids) {
            Setmeal setmeal = setmealMapper.queryById(id);
            if(setmeal.getStatus().equals(StatusConstant.ENABLE))
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

        for(Long id: ids) setmealMapper.deleteById(id);

        for(Long id: ids) setmealDishMapper.deleteBySetmealId(id);
    }

    @Transactional
    public void updateSetmealWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.update(setmeal);

        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(!setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    public void updateStatus(Long id, Integer status) {
        if(status == 1) {
            List<SetmealDish> setmealDishes = setmealDishMapper.querySetmealDishesBySetmealId(id);
            for(SetmealDish setmealDish: setmealDishes) {
                Dish dish = dishMapper.getById(setmealDish.getDishId());
                if (dish.getStatus() == 0) throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.query(setmeal);
    }

    public List<DishItemVO> getDishesBySetmealId(Long id) {
        List<SetmealDish> setmealDishes = setmealDishMapper.querySetmealDishesBySetmealId(id);
        List<DishItemVO> dishItemVOS = new ArrayList<>();
        for(SetmealDish setmealDish: setmealDishes) {
            DishItemVO dishItemVO = new DishItemVO();
            BeanUtils.copyProperties(setmealDish, dishItemVO);
            Dish dish = dishMapper.getById(setmealDish.getDishId());
            dishItemVO.setDescription(dish.getDescription());
            dishItemVO.setImage(dish.getImage());
            dishItemVOS.add(dishItemVO);
        }
        return dishItemVOS;
    }

}
