package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;


@RestController("userOrderController")
@Slf4j
@Api(tags = "订单相关接口")
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("提交订单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO =  orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @GetMapping("/historyOrders")
    public Result<PageResult> getHistoryResult(Integer page, Integer pageSize, @RequestParam(required = false) Integer status, ServletRequest servletRequest) {
        log.info("订单分页查询：{}, {}, {}", page, pageSize, status);
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setStatus(status);
        PageResult result = orderService.page(ordersPageQueryDTO, BaseContext.getCurrentId());
        return Result.success(result);
    }

    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) {
        log.info("取消订单：{}", id);
        OrdersCancelDTO ordersCancelDTO = new OrdersCancelDTO();
        ordersCancelDTO.setId(id);
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getOrderDetailById(@PathVariable Long id) {
        log.info("查询订单详情：{}", id);
        OrderVO orderVO = orderService.getOrderVOById(id);
        return Result.success(orderVO);
    }

    @PostMapping("/repetition/{id}")
    public Result repeatOrder(@PathVariable Long id) {
         log.info("再来一单：{}", id);
         orderService.repeat(id);
         return Result.success();
    }

    @PutMapping("/payment")
    public Result pay(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("支付订单：{}", ordersPaymentDTO);
        orderService.payOrder(ordersPaymentDTO);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id) {
        log.info("客户催单：{}", id);
        orderService.reminder(id);
        return Result.success();
    }

}
