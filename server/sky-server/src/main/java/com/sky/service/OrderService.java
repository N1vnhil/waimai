package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    OrderSubmitVO submit(OrdersSubmitDTO ordersDTO);

    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO, Long userId);

    OrderVO getOrderVOById(Long id);

    void repeat(Long id);

    OrderStatisticsVO getStatistics();

    void completeOrder(Long id);

    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    void deliverOrder(Long id);

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void payOrder(OrdersPaymentDTO ordersPaymentDTO);

    void reminder(Long id);
}
