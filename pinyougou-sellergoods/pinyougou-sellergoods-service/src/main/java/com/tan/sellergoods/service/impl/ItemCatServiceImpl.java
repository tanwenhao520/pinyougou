package com.tan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.dao.ItemCatMapper;
import com.tan.pojo.TbItemCat;
import com.tan.sellergoods.service.ItemCatService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Transactional
@Service(interfaceClass = ItemCatService.class)
public class ItemCatServiceImpl extends BaseServiceImpl<TbItemCat> implements ItemCatService {

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbItemCat itemCat) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(itemCat.getParentId())){
            criteria.andLike("paren_id", "%" + itemCat.getParentId() + "%");
        }*/

        List<TbItemCat> list = itemCatMapper.selectByExample(example);
        PageInfo<TbItemCat> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void deleteByIds(Long[] ids) {

    }
}
