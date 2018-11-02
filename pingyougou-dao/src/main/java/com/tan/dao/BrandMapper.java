package com.tan.dao;

import com.tan.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<TbBrand> {

    public List<TbBrand> findAll();

    List<Map<String,Object>> selectOptionList();
}
