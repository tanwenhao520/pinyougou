package com.tan.sellergoods.service;

import com.tan.pojo.TbGoods;
import com.tan.vo.BaseService;
import com.tan.vo.Goods;
import com.tan.vo.PageResult;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    void deleteByIds(String[] ids);

    void add(Goods goods);
}