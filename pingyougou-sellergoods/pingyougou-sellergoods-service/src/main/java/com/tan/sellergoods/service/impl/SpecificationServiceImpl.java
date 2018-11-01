package com.tan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.dao.SpecificationMapper;
import com.tan.dao.SpecificationOptionMapper;
import com.tan.pojo.TbSpecification;
import com.tan.pojo.TbSpecificationOption;
import com.tan.sellergoods.service.SpecificationService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import com.tan.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service(interfaceClass = SpecificationService.class)
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSpecification specification) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(specification.getSpecName())){
            criteria.andLike("specName", "%" + specification.getSpecName() + "%");
        }

        List<TbSpecification> list = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void add(Specification specification) {

        specificationMapper.insertSelective(specification.getSpecification());

        if(specification.getSpecificationOptionList() != null &&specification.getSpecificationOptionList().size() >0){
            for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
                tbSpecificationOption.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(tbSpecificationOption);
            }
        }

    }

    @Override
    public void deleteByIds(Long[] ids) {

    }
}
