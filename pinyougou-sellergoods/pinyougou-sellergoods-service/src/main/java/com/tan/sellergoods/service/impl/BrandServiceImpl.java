package com.tan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.dao.BrandMapper;
import com.tan.pojo.TbBrand;
import com.tan.sellergoods.service.BrandService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
@Transactional
@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl extends BaseServiceImpl<TbBrand> implements BrandService{

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.findAll();
    }

    @Override
    public List<TbBrand> testPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        return  brandMapper.selectAll();
    }

    /**
     * 条件查询并分页
     * @param page
     * @param rows
     * @param tbBrand
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, TbBrand tbBrand) {
        PageHelper.startPage(page,rows);
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(tbBrand.getFirstChar())){
            criteria.andEqualTo("firstChar",tbBrand.getFirstChar());
        }
        if(!StringUtils.isEmpty(tbBrand.getName())){
            criteria.andLike("name","%"+tbBrand.getName()+"%");
        }
        List<TbBrand> tbBrands = brandMapper.selectByExample(example);
        PageInfo<TbBrand> tbBrandPageInfo = new PageInfo<>(tbBrands);
        return new PageResult(tbBrandPageInfo.getTotal(),tbBrandPageInfo.getList());
    }

    /**
     * 查询所有品牌(由于分页助手无法修改字段名，所以调用的是Mapper映射文件的实现方法)
     * @return
     */
    @Override
    public List<Map<String, Object>> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
