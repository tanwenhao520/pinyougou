package com.tan.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.tan.common.HttpClient;
import com.tan.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service(interfaceClass = WeixinPayService.class)
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String mch_id;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notify_url;


    @Override
    public Map<String, String> createNative(Long outTradeNo, String totalFee) {
        Map<String,String> resultMap = new HashMap<>();

        try {
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid",appid);//从微信申请的公众账号 ID
            paramMap.put("mch_id",mch_id);//从微信申请的商户号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            paramMap.put("body","品优购");// 商品描述 - 可以设置为商品的标题
            paramMap.put("out_trade_no",outTradeNo+"");//订单号
            paramMap.put("total_fee",totalFee);//订单交易总金额
            paramMap.put("spbill_create_ip","127.0.0.1");//终端IP
            paramMap.put("notify_url",notify_url);//回调地址
            paramMap.put("trade_type","NATIVE");//付款方式
            //将参数Map转换为微信支付需要的xml
            String signeXml = WXPayUtil.generateSignedXml(paramMap,partnerkey);
            System.out.println("发送到微信统一下单的参数为"+signeXml);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signeXml);
            httpClient.post();
            String content = httpClient.getContent();
            System.out.println("微信统一下单返回的内容为:"+content);
            Map<String, String> resultMap2 = WXPayUtil.xmlToMap(content);
            resultMap.put("result_code",resultMap2.get("result_code"));
            resultMap.put("code_url",resultMap2.get("code_url"));
            resultMap.put("outTradeNo",outTradeNo+"");
            resultMap.put("totalFee",totalFee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public Map<String, String> queryPayStatus(Long outTradeNo) {
        try {
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid",appid);
            paramMap.put("mch_id",mch_id);
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            paramMap.put("out_trade_no",outTradeNo+"");//订单号
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("发送到微信支付查看订单的内容为"+signedXml);

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            //4 、获取微信支付返回的数据
            String content = httpClient.getContent();
            //转换为Map并设置返回
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> closeOrder(Long outTradeNo) throws Exception {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",mch_id);
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no",outTradeNo+"");
        String context = WXPayUtil.mapToXml(paramMap);
        System.out.println("发送到微信支付关闭订单的内容为："+context);
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
        httpClient.setHttps(true);
        httpClient.setXmlParam(context);
        httpClient.post();
        String content = httpClient.getContent();
        System.out.println("微信关闭订单返回的内容为:"+content);
        return WXPayUtil.xmlToMap(content);
    }
}
