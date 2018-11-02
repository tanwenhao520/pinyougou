package com.tan.sellergoods.service;

        import com.tan.pojo.TbBrand;
        import com.tan.vo.BaseService;
        import com.tan.vo.PageResult;

        import java.util.List;
        import java.util.Map;

public interface BrandService extends BaseService<TbBrand> {

    //List<TbBrand> findAll();

    List<TbBrand> testPage(Integer page, Integer rows);

    PageResult search(Integer page, Integer rows, TbBrand tbBrand);

    List<Map<String,Object>> selectOptionList();
}
