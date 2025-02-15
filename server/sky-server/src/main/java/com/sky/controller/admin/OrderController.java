package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminOrderController")
@Slf4j
@Api("服务端订单相关接口")
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PutMapping("/cancel")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("商家取消订单：{}", ordersCancelDTO);
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> getStatistics() {
        log.info("获取统计信息");
        return Result.success(orderService.getStatistics());
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        log.info("查询订单详情：{}", id);
        return Result.success(orderService.getOrderVOById(id));
    }

    @GetMapping("/conditionSearch")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单分页查询：{}", ordersPageQueryDTO);
        PageResult result = orderService.page(ordersPageQueryDTO, null);
        return Result.success(result);
    }

    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id) {
        log.info("完成订单：{}", id);
        orderService.completeOrder(id);
        return Result.success();
    }

    @PutMapping("/rejection")
    public Result reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单：{}", ordersRejectionDTO);
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单：{}", ordersConfirmDTO);
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result deliver(@PathVariable Long id) {
        log.info("配送：{}", id);
        orderService.deliverOrder(id);
        return Result.success();
    }

}
