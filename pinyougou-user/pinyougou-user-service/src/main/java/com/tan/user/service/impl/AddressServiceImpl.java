package com.tan.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.dao.AddressMapper;
import com.tan.pojo.TbAddress;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.user.service.AddressService;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service(interfaceClass = AddressService.class)
public class AddressServiceImpl extends BaseServiceImpl<TbAddress> implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbAddress address) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbAddress.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(address.get***())){
            criteria.andLike("***", "%" + address.get***() + "%");
        }*/

        List<TbAddress> list = addressMapper.selectByExample(example);
        PageInfo<TbAddress> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
