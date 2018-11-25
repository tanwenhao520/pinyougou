package com.tan.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.order.service.OrderService;
import com.tan.pay.service.WeixinPayService;
import com.tan.pojo.TbPayLog;
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
    private OrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;

    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo){
       TbPayLog tbPayLog = orderService.findPayLogByOutTradeNo(outTradeNo);
       if(tbPayLog != null){
            return weixinPayService.createNative(outTradeNo,tbPayLog.getTotalFee().toString());
       }
       return new HashMap<>();
    }

    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo){
        Result result = Result.fail("支付失败!");
        try {
            int count = 0;
            while (true){
                Map<String,String> resultMap = weixinPayService.queryPayStatus(outTradeNo);
                if(resultMap == null){
                    break;
                }
                if("SUCCESS".equals(resultMap.get("result_code"))){
                    if("SUCCESS".equals(resultMap.get("trade_state"))){
                        result = Result.ok("支付成功!");
                        orderService.updateOrderStatus(outTradeNo,resultMap.get("transaction_id"));
                        break;
                    }
                }else{
                    result = Result.fail("支付失败");
                    break;
                }
                count++;
                if(count > 10){
                    result = Result.fail("二维码超时");
                    break;
                }
                //每3秒查询一次
                Thread.sleep(3000);

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
