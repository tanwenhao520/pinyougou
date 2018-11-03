package com.tan.sellergoods.service;

import com.tan.pojo.TbItemCat;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

public interface ItemCatService extends BaseService<TbItemCat> {

    PageResult search(Integer page, Integer rows, TbItemCat itemCat);

    void deleteByIds(Long[] ids);
}