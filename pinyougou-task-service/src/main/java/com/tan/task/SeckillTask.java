package com.tan.task;

import com.tan.dao.SeckillGoodsMapper;
import com.tan.pojo.TbGoods;
import com.tan.pojo.TbSeckillGoods;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;


    public static final String SECKILL_GOODS="SECKILL_GOODS";

    /**
     * 任务调度，没秒从数据库中查询Redis没有的秒杀商品，并添加到Redis秒杀商品中
     */
    @Scheduled(cron = "* * * * * ?")
    public void refreshSeckillGoods(){
        Set ids = redisTemplate.boundHashOps(SECKILL_GOODS).keys();

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");
        criteria.andLessThanOrEqualTo("startTime",new Date());
        criteria.andGreaterThan("endTime",new Date());
        criteria.andGreaterThan("stockCount",0);
        if(ids.size() >0){
            criteria.andNotIn("id",ids);
        }
        List<TbSeckillGoods> tbSeckillGoods = seckillGoodsMapper.selectByExample(example);
        if(tbSeckillGoods.size() > 0 ){
            for (TbSeckillGoods tbSeckillGood : tbSeckillGoods) {
                redisTemplate.boundHashOps(SECKILL_GOODS).put(tbSeckillGood.getId(),tbSeckillGood);
            }
        }
        System.out.println("已将"+tbSeckillGoods.size()+"条数据导入Redis秒杀商品中");

    }

    /**
     * 每秒定时查看Redis中的秒杀商品是否过期，如过期则删除，并将数据更新到Mysql中
     */
    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods(){
        //读取Redis中的秒杀商品
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps(SECKILL_GOODS).values();
        //判断是否有数据
        if(seckillGoods != null  && seckillGoods.size() > 0 ){
            for (TbSeckillGoods seckillGood : seckillGoods) {
                //判断时间是否小于现在时间，如果是则是过期秒杀商品
                if(seckillGood.getEndTime().getTime() < new Date().getTime()){
                    //删除Redis中的数据
                    redisTemplate.boundHashOps(SECKILL_GOODS).delete(seckillGood.getId(),seckillGood);
                    //将最新的数据更新到Mysql
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGood);
                }
            }
        }
        System.out.println("已将过期的商品删除");
    }


}
