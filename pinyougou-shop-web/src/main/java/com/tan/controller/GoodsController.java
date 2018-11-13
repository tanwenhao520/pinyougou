package com.tan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.pojo.TbGoods;
import com.tan.sellergoods.service.GoodsService;
import com.tan.vo.Goods;
import com.tan.vo.PageResult;
import com.tan.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 查询所有   [暂无使用]
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    /**
     * 查询分页(不带参数)  [暂无使用]
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {

        return goodsService.findPage(page, rows);
    }

    /**
     * 增加商品，操作商品(SPU)规格表、(SPU)商品描述表、(SKU)商品表，再增加SPU商品规格，商品描述的同时，增加描述以及SKU商品表
     * @param goods
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            goods.getGoods().setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
            goods.getGoods().setAuditStatus("0");
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 修改时回显数据
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(name);
        return goodsService.search(page, rows, goods);
    }

    /**
     * 修改状态
     * @param ids
     * @param status
     * @return
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids ,String status){
        try {
            goodsService.updateStatus(ids,status);
            return Result.ok("提交审核成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("提交审核失败！");
    }

    /**
     * 商品上下架(暂无搞定)
     * @param ids
     * @param status
     * @return
     */
    @GetMapping("/updateis_marketable")
    public Result updateis_markeTable(Long[] ids,String status){
        try {
            goodsService.updateis_markeTable(ids,status);
            return Result.ok("商品上架成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("商品上架失败!");
    }




}
