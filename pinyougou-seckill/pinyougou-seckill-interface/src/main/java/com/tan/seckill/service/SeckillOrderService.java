package com.tan.seckill.service;

import com.tan.pojo.TbSeckillOrder;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

public interface SeckillOrderService extends BaseService<TbSeckillOrder> {

    PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder);

    TbSeckillOrder getSeckillOrderInRedisByOrderId(Long outTradeNo);

    void saveOrderInRedisToDB(Long outTradeNo,String transaction_id);

    void deleteOrderByRedis(Long outTradeNo) throws InterruptedException;
}