package com.tan.vo;

import com.tan.pojo.TbSpecification;
import com.tan.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

public class Specification implements Serializable {

    private TbSpecification specification;

    private List<TbSpecificationOption> specificationOptionList;

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
