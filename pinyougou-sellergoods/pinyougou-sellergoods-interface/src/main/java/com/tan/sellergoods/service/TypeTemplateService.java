package com.tan.sellergoods.service;

import com.tan.pojo.TbTypeTemplate;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);

    void deleteByIds(Long[] ids);

    List<Map> findSpecList(Long id);
}