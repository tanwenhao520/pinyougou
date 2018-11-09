package com.tan.content.service;

import com.tan.pojo.TbContentCategory;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

public interface ContentCategoryService extends BaseService<TbContentCategory> {

    PageResult search(Integer page, Integer rows, TbContentCategory contentCategory);

}