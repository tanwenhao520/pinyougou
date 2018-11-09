package com.tan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.dao.SpecificationOptionMapper;
import com.tan.dao.TypeTemplateMapper;
import com.tan.pojo.TbSpecificationOption;
import com.tan.pojo.TbTypeTemplate;
import com.tan.sellergoods.service.TypeTemplateService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Transactional
@Service(interfaceClass = TypeTemplateService.class)
public  class TypeTemplateServiceImpl extends BaseServiceImpl<TbTypeTemplate> implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询模板并分页带条件，
     * @param page 当前页数
     * @param rows 页大小
     * @param typeTemplate 查询条件
     * @return 结果集(总页数、数据列表)
     */
    @Override
    public PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(typeTemplate.getName())){
            criteria.andLike("name", "%" + typeTemplate.getName() + "%");
        }

        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void deleteByIds(Long[] ids) {
            super.deleteByIds(ids);
    }

    @Override
    public List<Map> findSpecList(Long id) {
        TbTypeTemplate tbTypeTemplate = findOne(id);
        List<Map> map = JSONArray.parseArray(tbTypeTemplate.getSpecIds(),Map.class);
        for (Map map1 : map) {
            TbSpecificationOption tso = new TbSpecificationOption();
            tso.setSpecId(Long.parseLong(map1.get("id").toString()));
            List<TbSpecificationOption> select = specificationOptionMapper.select(tso);
            map1.put("options",select);
        }
        return map;
    }


}
