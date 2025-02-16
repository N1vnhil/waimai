package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> list = new ArrayList<>();

        list.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            list.add(begin);
        }

        List<BigDecimal> turnovers = new ArrayList<>();
        list.forEach(date -> {
            LocalDateTime beginDayTime = LocalDateTime.of(date, LocalTime.MIN), endDayTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginDayTime);
            map.put("end", endDayTime);
            map.put("status", Orders.COMPLETED);
            BigDecimal totalAmount = orderMapper.sumByMap(map);
            totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
            turnovers.add(totalAmount);
        });

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(list, ","))
                .turnoverList(StringUtils.join(turnovers, ","))
                .build();
    }

    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> list = new ArrayList<>();

        list.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            list.add(begin);
        }

        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        Map tmp = new HashMap();
        tmp.put("end", LocalDateTime.of(begin, LocalTime.MIN));
        Integer curTotalUser = userMapper.countByMap(tmp);
        curTotalUser = curTotalUser != null ? curTotalUser : 0;

        for (LocalDate date: list) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("end", endTime);
            map.put("begin", beginTime);

            Integer newUsers = userMapper.countByMap(map);
            newUsers = newUsers != null ? newUsers : 0;
            curTotalUser += newUsers;

            newUserList.add(newUsers);
            totalUserList.add(curTotalUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(list, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> list = new ArrayList<>();
        list.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            list.add(begin);
        }

        List<Integer> ordersList = new ArrayList<>();
        List<Integer> validOrdersList = new ArrayList<>();

        Integer totalOrders = 0, validOrders = 0;

        for (LocalDate date: list) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Integer orderCount = getOrderCount(beginTime, endTime, null);
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            ordersList.add(orderCount);
            validOrdersList.add(validOrderCount);
            totalOrders += orderCount;
            validOrders += validOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(list, ","))
                .validOrderCountList(StringUtils.join(validOrdersList, ","))
                .orderCountList(StringUtils.join(ordersList, ","))
                .totalOrderCount(totalOrders)
                .validOrderCount(validOrders)
                .orderCompletionRate(validOrders.doubleValue() / totalOrders.doubleValue())
                .build();
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }

    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> saleTop10 = orderMapper.getTop10(beginTime, endTime);
        List<String> names = saleTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        List<Integer> numbers = saleTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
