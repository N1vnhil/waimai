package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

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

}
