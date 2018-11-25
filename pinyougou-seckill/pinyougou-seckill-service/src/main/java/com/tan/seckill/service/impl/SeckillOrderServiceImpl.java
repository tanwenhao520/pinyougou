package com.tan.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.common.RedisLock;
import com.tan.dao.SeckillGoodsMapper;
import com.tan.dao.SeckillOrderMapper;
import com.tan.pojo.TbSeckillGoods;
import com.tan.pojo.TbSeckillOrder;
import com.tan.seckill.service.SeckillOrderService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillOrderService.class)
public class SeckillOrderServiceImpl extends BaseServiceImpl<TbSeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper SeckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String SECKILL_ORDERS = "SECKILL_ORDERS";

    public static final String SECKILL_GOODS = "SECKILL_GOODS";

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillOrder.get***())){
            criteria.andLike("***", "%" + seckillOrder.get***() + "%");
        }*/

        List<TbSeckillOrder> list = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public TbSeckillOrder getSeckillOrderInRedisByOrderId(Long outTradeNo) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SECKILL_ORDERS).get(outTradeNo);
    }

    /**
     * 到Redis中查询订单号为outTradeNo的订单，如果订单不存在或者两个订单不一样都回抛出一个运行时异常,如果存在则持久化到Mysql并把Redis中的订单删除
     * @param outTradeNo 订单号
     * @param transaction_id 微信返回的订单号
     */
    @Override
    public void saveOrderInRedisToDB(Long outTradeNo,String transaction_id) {
        TbSeckillOrder tbSeckillOrder = getSeckillOrderInRedisByOrderId(outTradeNo);
        if(tbSeckillOrder == null){
            throw new RuntimeException("订单不存在");
        }
        if(!outTradeNo.equals(tbSeckillOrder.getId())){
            throw new RuntimeException("订单不符合");
        }
        tbSeckillOrder.setPayTime(new Date());
        tbSeckillOrder.setTransactionId(transaction_id);
        tbSeckillOrder.setStatus("1");//已支付
        //如果查询到Redis有该商品的存在则将该订单持久化到数据库
        seckillOrderMapper.insertSelective(tbSeckillOrder);
        //删除Redis中的缓存
        redisTemplate.boundHashOps(SECKILL_ORDERS).delete(outTradeNo);
    }

    /**
     * 支付超时则删除Redis中outTradeNo订单的缓存，并将Redis商品的数量加1(如果为0时则到数据库查询，如果在则数量加1保存到Redis中)
     * @param outTradeNo
     * @throws InterruptedException
     */
    @Override
    public void deleteOrderByRedis(Long outTradeNo) throws InterruptedException {
        TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SECKILL_ORDERS).get(outTradeNo);
        if(tbSeckillOrder != null && outTradeNo.equals(tbSeckillOrder.getId())){
            redisTemplate.boundHashOps(SECKILL_ORDERS).delete(outTradeNo);
            RedisLock redisLock = new RedisLock(redisTemplate);
            if(redisLock.lock(tbSeckillOrder.getSeckillId().toString())){
                TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(tbSeckillOrder.getSeckillId());
                if(tbSeckillGoods == null){
                    TbSeckillGoods tbSeckillGoods1 = SeckillGoodsMapper.selectByPrimaryKey(tbSeckillOrder.getSeckillId());
                    tbSeckillGoods1.setStockCount(tbSeckillGoods1.getStockCount()+1);
                    redisTemplate.boundHashOps(SECKILL_GOODS).put(tbSeckillGoods1.getId(),tbSeckillGoods1);
                    //释放分布式锁
                    redisLock.unlock(tbSeckillOrder.getSeckillId().toString());
                }
            }
        }

    }
}
