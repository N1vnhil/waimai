package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import com.sky.entity.Orders;

import java.time.LocalDateTime;

@Component
@Slf4j
public class Task {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * *")
    public void processTimeOutOrder() {
        log.info("处理超时订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orderList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);
        if(orderList != null && !orderList.isEmpty()) {
            orderList.forEach(o -> {
                o.setStatus(Orders.CANCELLED);
                o.setCancelReason("订单支付超时.");
                o.setCancelTime(LocalDateTime.now());
                orderMapper.update(o);
            });
        }

    }

    @Scheduled(cron = "0 0 1 * * ? ")
    public void processCompletedOrder() {
        log.info("结清配送中订单");
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);
        if(ordersList != null && !ordersList.isEmpty()) {
            ordersList.forEach(o -> {
                o.setDeliveryTime(LocalDateTime.now());
                o.setStatus(Orders.COMPLETED);
                orderMapper.update(o);
            });
        }

    }

}
