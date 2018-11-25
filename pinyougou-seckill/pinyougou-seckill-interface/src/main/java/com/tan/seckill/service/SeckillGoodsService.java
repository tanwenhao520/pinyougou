package com.tan.seckill.service;

import com.tan.pojo.TbSeckillGoods;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

import java.util.List;

public interface SeckillGoodsService extends BaseService<TbSeckillGoods> {

    PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods);

    List<TbSeckillGoods> findList();

    TbSeckillGoods findOneByRedis(Long id);

    Long submitOrder(Long seckillId, String name) throws InterruptedException;
}