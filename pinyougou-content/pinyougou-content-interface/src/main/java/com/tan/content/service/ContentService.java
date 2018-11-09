package com.tan.content.service;

import com.tan.pojo.TbContent;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

import java.util.List;

public interface ContentService extends BaseService<TbContent> {

    PageResult search(Integer page, Integer rows, TbContent content);

    List<TbContent> findContentListByCategoryId(Long id);
}