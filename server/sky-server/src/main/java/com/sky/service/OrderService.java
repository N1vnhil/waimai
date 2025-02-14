package com.sky.service;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    OrderSubmitVO submit(OrdersSubmitDTO ordersDTO);

    void cancelOrder(Long id);

    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO getOrderVOById(Long id);

    void repeat(Long id);
}
