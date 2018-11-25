package com.tan.order.service;

import com.tan.pojo.TbOrderItem;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

public interface OrderItemService extends BaseService<TbOrderItem> {

    PageResult search(Integer page, Integer rows, TbOrderItem orderItem);
}