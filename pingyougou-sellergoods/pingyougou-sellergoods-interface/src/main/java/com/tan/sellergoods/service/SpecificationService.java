package com.tan.sellergoods.service;

import com.tan.pojo.TbSpecification;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;
import com.tan.vo.Specification;

public interface SpecificationService extends BaseService<TbSpecification> {

    PageResult search(Integer page, Integer rows, TbSpecification specification);

    void add(Specification specification);

    void deleteByIds(Long[] ids);
}