package com.tan.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.common.IdWorker;
import com.tan.common.RedisLock;
import com.tan.dao.SeckillGoodsMapper;
import com.tan.pojo.TbSeckillGoods;
import com.tan.pojo.TbSeckillOrder;
import com.tan.seckill.service.SeckillGoodsService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillGoodsService.class)
public class SeckillGoodsServiceImpl extends BaseServiceImpl<TbSeckillGoods> implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String SECKILL_GOODS = "SECKILL_GOODS";


    public static final String SECKILL_ORDERS = "SECKILL_ORDERS";

    @Autowired
    private IdWorker idWorker;

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillGoods.get***())){
            criteria.andLike("***", "%" + seckillGoods.get***() + "%");
        }*/

        List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example);
        PageInfo<TbSeckillGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbSeckillGoods> findList() {
        List<TbSeckillGoods> tbSeckillGoods;
        tbSeckillGoods = redisTemplate.boundHashOps(SECKILL_GOODS).values();
        if(tbSeckillGoods.size() == 0 || tbSeckillGoods == null){
            Example example = new Example(TbSeckillGoods.class);
            example.createCriteria().andEqualTo("status","1")
                    .andGreaterThan("stockCount",0)
                    .andLessThanOrEqualTo("startTime",new Date())
                    .andGreaterThan("endTime",new Date());
            example.orderBy("startTime");
            tbSeckillGoods = seckillGoodsMapper.selectByExample(example);
            System.out.println(tbSeckillGoods+"........................................");
            //如果不为空时则放入Redis
            if(tbSeckillGoods != null){
                for (TbSeckillGoods tbSeckillGood : tbSeckillGoods) {
                    redisTemplate.boundHashOps(SECKILL_GOODS).put(tbSeckillGood.getId(),tbSeckillGood);
                }
            }
        }else{
            System.out.println("从缓存中读取商品数据.......");
        }
        System.out.println(tbSeckillGoods+"****************************");
        return tbSeckillGoods;
    }

    @Override
    public TbSeckillGoods findOneByRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(id);
    }

    @Override
    public Long submitOrder(Long seckillId, String name) throws InterruptedException {
        RedisLock redisLock = new RedisLock(redisTemplate);
        if(redisLock.lock(seckillId.toString())){
            TbSeckillGoods tbSeckillGoods = (TbSeckillGoods)redisTemplate.boundHashOps(SECKILL_GOODS).get(seckillId);
            if(tbSeckillGoods == null){
                throw new RuntimeException("商品不存在");
            }
            if(tbSeckillGoods.getStockCount() == 0 ){
                throw new RuntimeException("商品已抢完");
            }
            tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount() -1);
            if(tbSeckillGoods.getStockCount() > 0 ){
                redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillId,tbSeckillGoods);
            }else{
                seckillGoodsMapper.updateByPrimaryKeySelective(tbSeckillGoods);
                redisTemplate.boundHashOps(SECKILL_GOODS).delete(seckillId);
            }

            redisLock.unlock(seckillId.toString());

            Long orderId = idWorker.nextId();
            TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
            tbSeckillOrder.setId(orderId);//随机ID
            tbSeckillOrder.setCreateTime(new Date());
            tbSeckillOrder.setStatus("0");//未支付
            tbSeckillOrder.setUserId(name);//买家名称
            tbSeckillOrder.setSeckillId(seckillId);//商品ID
            tbSeckillOrder.setSellerId(tbSeckillGoods.getSellerId());//商家ID
            tbSeckillOrder.setMoney(tbSeckillGoods.getCostPrice());//秒杀价

            redisTemplate.boundHashOps(SECKILL_ORDERS).put(orderId,tbSeckillOrder);

            return orderId;
        }
        return null;
    }
}
