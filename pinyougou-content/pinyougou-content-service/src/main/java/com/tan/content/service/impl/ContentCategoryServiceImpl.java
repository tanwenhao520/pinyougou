package com.tan.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.content.service.ContentCategoryService;
import com.tan.dao.ContentCategoryMapper;
import com.tan.pojo.TbContentCategory;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service(interfaceClass = ContentCategoryService.class)
public class ContentCategoryServiceImpl extends BaseServiceImpl<TbContentCategory> implements ContentCategoryService {

    @Autowired
    private ContentCategoryMapper contentCategoryMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbContentCategory contentCategory) {

        /**
         * 课堂测试
         */
       /* Example example1 = new Example(TbItem.class);
        example1.createCriteria().andIn("goodsId", Arrays.asList(ids)).andEqualTo("status","1");

        itemMapper.selectByExample(example);*/


        PageHelper.startPage(page, rows);

        Example example = new Example(TbContentCategory.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(contentCategory.getName())){
            criteria.andLike("name", "%" + contentCategory.getName() + "%");
        }

        List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
        PageInfo<TbContentCategory> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }



}
