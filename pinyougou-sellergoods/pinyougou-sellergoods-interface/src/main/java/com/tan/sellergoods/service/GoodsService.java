package com.tan.sellergoods.service;

import com.tan.pojo.TbGoods;
import com.tan.vo.BaseService;
import com.tan.vo.Goods;
import com.tan.vo.PageResult;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);


    void add(Goods goods);

    void addGoods(Goods goods);

    void updateStatus(Long[] ids ,String status);

    Goods findGoodsById(Long id);

    void update(Goods goods);


    void deleteGoodsByIds(String[] ids);
}