package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    void update(Orders orders);

    List<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    List<Orders> query(Orders orders);

    @Select("select * from orders where status=#{status} and order_time < #{localDateTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime localDateTime);

    BigDecimal sumByMap(Map map);

    Integer countByMap(Map map);

    List<GoodsSalesDTO> getTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
