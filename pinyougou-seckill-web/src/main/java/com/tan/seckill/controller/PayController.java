package com.tan.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.pay.service.WeixinPayService;
import com.tan.pojo.TbSeckillOrder;
import com.tan.seckill.service.SeckillOrderService;
import com.tan.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @Reference
    private WeixinPayService weixinPayService;

    @GetMapping("/createNative")
    public Map<String,String> createNative(Long outTradeNo){
        System.out.println(outTradeNo);
        // 根据订单 id 查询放置在 redis 中的订单
        TbSeckillOrder tbSeckillOrder = seckillOrderService.getSeckillOrderInRedisByOrderId(outTradeNo);
        System.out.println(tbSeckillOrder);
        if(tbSeckillOrder != null){
            //总价格
           String totalFee =  (long)(tbSeckillOrder.getMoney().doubleValue() * 100)+"";
           return weixinPayService.createNative(outTradeNo,totalFee);
        }
        return new HashMap<>();
    }

    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(Long outTradeNo){
        Result result = Result.fail("支付失败!");
        int count = 0;
        try {
            while (true){
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);
                if(resultMap == null){
                    break;
                }
                if("SUCCESS".equals(resultMap.get("trade_state"))){
                    seckillOrderService.saveOrderInRedisToDB(outTradeNo,resultMap.get("transaction_id"));
                    result = Result.ok("支付成功!");
                    break;
                }
                Thread.sleep(3000);
                count++;
                if(count > 10){
                    result = Result.fail("支付超时");
                    resultMap =  weixinPayService.closeOrder(outTradeNo);
                    if("ORDERPAID".equals(resultMap.get("err_code"))){
                        result = Result.ok("支付成功");
                        seckillOrderService.saveOrderInRedisToDB(outTradeNo,resultMap.get("transaction_id"));
                        break;
                    }

                    seckillOrderService.deleteOrderByRedis(outTradeNo);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
