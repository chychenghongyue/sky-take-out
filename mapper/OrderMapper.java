package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    @Select("select * from orders where number=#{outTradeNo}")
    Orders getByNumber(String outTradeNo);

    void update(Orders orders);

    @Select("select * from orders where status = #{status} and order_time < #{localDateTime}")
    List<Orders> getOrderTask(Integer status, LocalDateTime localDateTime);

    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

}
