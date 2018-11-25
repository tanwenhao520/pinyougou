package com.tan.pay.service;

import java.util.Map;

public interface WeixinPayService {
    Map<String,String> createNative(Long outTradeNo, String totalFee);

    Map<String, String> queryPayStatus(Long outTradeNo);

    Map<String,String> closeOrder(Long outTradeNo) throws Exception;
}
