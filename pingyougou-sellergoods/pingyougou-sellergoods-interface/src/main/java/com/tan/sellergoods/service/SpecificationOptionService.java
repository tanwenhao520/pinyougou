package com.tan.sellergoods.service;

import com.tan.pojo.TbSpecificationOption;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

public interface SpecificationOptionService extends BaseService<TbSpecificationOption> {

    PageResult search(Integer page, Integer rows, TbSpecificationOption specificationOption);

    void deleteByIds(Long[] ids);
}