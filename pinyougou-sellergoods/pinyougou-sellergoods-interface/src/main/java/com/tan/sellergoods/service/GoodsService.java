package com.tan.sellergoods.service;

import com.tan.pojo.TbGoods;
import com.tan.pojo.TbItem;
import com.tan.vo.BaseService;
import com.tan.vo.Goods;
import com.tan.vo.PageResult;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);


    void add(Goods goods);

    void addGoods(Goods goods);

    void updateStatus(Long[] ids ,String status);

    Goods findGoodsById(Long id);

    void update(Goods goods);


    void deleteGoodsByIds(Long[] ids);

    void updateis_markeTable(Long[] ids, String status);

    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);

    Goods findGoodsByIdAndStatus(Long goodsId,String status);
}