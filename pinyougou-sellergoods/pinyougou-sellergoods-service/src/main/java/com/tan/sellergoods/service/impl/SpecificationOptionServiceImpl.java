package com.tan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.dao.SpecificationOptionMapper;
import com.tan.pojo.TbSpecificationOption;
import com.tan.sellergoods.service.SpecificationOptionService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Transactional
@Service(interfaceClass = SpecificationOptionService.class)
public class SpecificationOptionServiceImpl extends BaseServiceImpl<TbSpecificationOption> implements SpecificationOptionService {

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSpecificationOption specificationOption) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSpecificationOption.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(specificationOption.get***())){
            criteria.andLike("***", "%" + specificationOption.get***() + "%");
        }*/

        List<TbSpecificationOption> list = specificationOptionMapper.selectByExample(example);
        PageInfo<TbSpecificationOption> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void deleteByIds(Long[] ids) {

    }
}
