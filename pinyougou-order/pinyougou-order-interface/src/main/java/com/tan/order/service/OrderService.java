package com.tan.order.service;

import com.tan.pojo.TbOrder;
import com.tan.pojo.TbPayLog;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    String addOrder(TbOrder order);

    TbPayLog findPayLogByOutTradeNo(String outTradeNo);

    void updateOrderStatus(String outTradeNo, String transaction_id);
}