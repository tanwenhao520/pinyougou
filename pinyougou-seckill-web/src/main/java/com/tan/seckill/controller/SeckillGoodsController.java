package com.tan.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.pojo.TbSeckillGoods;
import com.tan.seckill.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/seckillGoods")
@RestController
public class SeckillGoodsController {
    //设置远程服务调用超时时间(毫秒);默认1000
    @Reference(timeout = 10000)
    private SeckillGoodsService seckillGoodsService;


    @GetMapping("/findList")
    public List<TbSeckillGoods> findList(){
      List<TbSeckillGoods> list =  seckillGoodsService.findList();
      return list;
    }

    @GetMapping("/findOne")
    public TbSeckillGoods findOne(Long id){
        return  seckillGoodsService.findOneByRedis(id);
    }


}
