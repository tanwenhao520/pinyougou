package com.tan.sellergoods.service;

import com.tan.pojo.TbSeller;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

public interface SellerService extends BaseService<TbSeller> {

    PageResult search(Integer page, Integer rows, TbSeller seller);

    void deleteByIds(Long[] ids);
}