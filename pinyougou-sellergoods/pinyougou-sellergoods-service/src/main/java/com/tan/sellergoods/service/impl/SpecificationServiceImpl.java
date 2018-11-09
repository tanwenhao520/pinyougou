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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Transactional
@Service(interfaceClass = SpecificationService.class)
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService{

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

        if(specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size() >0){
            for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
                tbSpecificationOption.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(tbSpecificationOption);
            }
        }

    }

    /**
     * 重写deleteByIds方法，并调用父类的删除方法进行删除,在删除规格选项
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        if(ids.length > 0){

            super.deleteByIds(ids);

            Example example = new Example(TbSpecificationOption.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("specId", Arrays.asList(ids));
            specificationOptionMapper.deleteByExample(example);
        }

    }

    /**
     * 查询规格和规格选项表并回显给页面
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {

        Specification specification = new Specification();

        specification.setSpecification(specificationMapper.selectByPrimaryKey(id));

        TbSpecificationOption param = new TbSpecificationOption();
        param.setSpecId(id);

        List<TbSpecificationOption> select = specificationOptionMapper.select(param);
        System.out.println(select);

        specification.setSpecificationOptionList(select);
        return specification;
    }

    /**
     * 修改规格表，以及不确定管理员删除了哪个规格选项和新增了哪个规格选项，所以首先删除指定id的规格选项再新增
     * @param specification
     */
    @Override
    public void update(Specification specification) {
        specificationMapper.updateByPrimaryKeySelective(specification.getSpecification());

        TbSpecificationOption tfo = new TbSpecificationOption();
        tfo.setSpecId(specification.getSpecification().getId());
        specificationOptionMapper.delete(tfo);

        if(specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size()>0){
            for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
                tbSpecificationOption.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(tbSpecificationOption);
            }
        }
    }

    /**
     * 查询所有品牌(由于分页助手无法修改字段名，所以调用的是Mapper映射文件的实现方法)
     * @return
     */
    @Override
    public List<Map<String, Object>> selectOptionList() {
        return specificationMapper.selectOptionList();
    }


}
