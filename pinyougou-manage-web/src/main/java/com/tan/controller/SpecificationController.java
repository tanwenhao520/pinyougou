package com.tan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.pojo.TbSpecification;
import com.tan.sellergoods.service.SpecificationService;
import com.tan.vo.PageResult;
import com.tan.vo.Result;
import com.tan.vo.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/specification")
@RestController
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    @RequestMapping("/findAll")
    public List<TbSpecification> findAll() {
        return specificationService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return specificationService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Specification specification) {
        System.out.println(specification);
        try {
            specificationService.add(specification);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 查询规格表和规格选项表进行回显，让管理员操作
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public Specification findOne(Long id) {
        return specificationService.findOne(id);
    }

    /**
     * 管理员修改回显数据时，并点击保存按钮时，进行修改
     * @param specification
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Specification specification) {
        try {
            specificationService.update(specification);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     *管理员进行删除时，进行删除方法
     * @param ids
     * @return
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            specificationService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param specification 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSpecification specification, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return specificationService.search(page, rows, specification);
    }

    /**
     * 页面加载时加载下拉的属性(即查询所有品牌)
     * @return
     */
    @GetMapping("/selectOptionList")
    public List<Map<String,Object>> selectOptionList(){
        return specificationService.selectOptionList();
    }

}
