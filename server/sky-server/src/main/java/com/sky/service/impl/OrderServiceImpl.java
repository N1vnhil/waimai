package com.sky.service.impl;

import com.fasterxml.jackson.databind.cfg.CoercionConfig;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import com.sky.entity.Orders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 检查地址是否为空
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 检查购物车是否为空
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 插入订单表
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(addressBook.getUserId());
        orders.setAddress(addressBook.getDetail());
        orderMapper.insert(orders);

        // 插入订单明细表
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(ShoppingCart cart: shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        // 清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Long id = ordersCancelDTO.getId();
        Orders orders = orderMapper.getById(id);
        if(orders.getStatus().compareTo(Orders.TO_BE_CONFIRMED) >= 0) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setStatus(Orders.CANCELLED);
        if(orders.getPayStatus().equals(Orders.PAID)) {
            log.info("退款：{}", id);
            orders.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(orders);
    }

    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO, Long userId) {
        ordersPageQueryDTO.setPage(ordersPageQueryDTO.getPage() - 1);
        if(userId != null) ordersPageQueryDTO.setUserId(userId);

        List<Orders> orders = orderMapper.pageQuery(ordersPageQueryDTO);
        PageResult pageResult = new PageResult();

        if(orders == null || orders.isEmpty()) {
            pageResult.setTotal(0);
            return pageResult;
        }

        List<OrderVO> orderVOS = new ArrayList<>();
        orders.forEach(order -> {
            List<OrderDetail> orderDetails = orderDetailMapper.getOrderDetailByOrderId(order.getId());
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setOrderDetailList(orderDetails);
            orderVOS.add(orderVO);
        });

        pageResult.setRecords(orderVOS);
        pageResult.setTotal(orderVOS.size());
        return pageResult;
    }

    public OrderVO getOrderVOById(Long id) {
        OrderVO orderVO = new OrderVO();
        Orders orders = orderMapper.getById(id);
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailMapper.getOrderDetailByOrderId(id));
        return orderVO;
    }

    public void repeat(Long id) {
        List<OrderDetail> orderDetails = orderDetailMapper.getOrderDetailByOrderId(id);
        Long userId = BaseContext.getCurrentId();
        orderDetails.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCartMapper.insert(shoppingCart);
        });
    }

    public OrderStatisticsVO getStatistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        Orders orders = new Orders();
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        Integer toBeConfirmed = orderMapper.query(orders).size();

        orders.setStatus(Orders.CONFIRMED);
        Integer confirmed = orderMapper.query(orders).size();

        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer deliveryInProgress = orderMapper.query(orders).size();

        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    public void completeOrder(Long id) {
        Orders orders = orderMapper.getById(id);
        if(orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        if(!orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS) && !orders.getPayStatus().equals(Orders.PAID)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }

    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Long id = ordersRejectionDTO.getId();
        Orders orders = orderMapper.getById(id);

        if(orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if(!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orderMapper.update(orders);
    }

    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = orderMapper.getById(ordersConfirmDTO.getId());
        if(orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if(!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    public void deliverOrder(Long id) {
        Orders orders = orderMapper.getById(id);
        if(orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if(!orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    public void payOrder(OrdersPaymentDTO ordersPaymentDTO) {
        Orders orders = Orders.builder()
                .number(ordersPaymentDTO.getOrderNumber())
                .payMethod(ordersPaymentDTO.getPayMethod())
                .build();
        List<Orders> list = orderMapper.query(orders);
        if(list == null || list.isEmpty()) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        orders = list.get(0);
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setPayStatus(Orders.PAID);
        orderMapper.update(orders);
    }
}
