package com.tan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.dao.SellerMapper;
import com.tan.pojo.TbSeller;
import com.tan.sellergoods.service.SellerService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Transactional
@Service(interfaceClass = SellerService.class)
public class SellerServiceImpl extends BaseServiceImpl<TbSeller> implements SellerService {

    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSeller seller) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeller.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(seller.getName())){
            criteria.andLike("name", "%" + seller.getName() + "%");
        }
        if(!StringUtils.isEmpty(seller.getNickName())){
            criteria.andLike("nickName", "%" + seller.getNickName() + "%");
        }
        if(!StringUtils.isEmpty(seller.getStatus())){
            criteria.andEqualTo("status",  seller.getStatus());
        }
        List<TbSeller> list = sellerMapper.selectByExample(example);
        PageInfo<TbSeller> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void deleteByIds(Long[] ids) {

    }
}
