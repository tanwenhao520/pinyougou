package com.tan.user.service;

import com.tan.pojo.TbAddress;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

public interface AddressService extends BaseService<TbAddress> {
    PageResult search(Integer page, Integer rows, TbAddress address);
}
