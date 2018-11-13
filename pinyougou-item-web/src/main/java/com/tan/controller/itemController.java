package com.tan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.pojo.TbItemCat;
import com.tan.sellergoods.service.GoodsService;
import com.tan.sellergoods.service.ItemCatService;
import com.tan.vo.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class itemController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @GetMapping("/{goodsId}")
    public ModelAndView toItemPage(@PathVariable Long goodsId){
        System.out.println(goodsId);

        ModelAndView mv = new ModelAndView("item");

        Goods goods = goodsService.findGoodsByIdAndStatus(goodsId,"1");

        mv.addObject("goods",goods.getGoods());

        mv.addObject("goodsDesc",goods.getGoodsDesc());

        mv.addObject("itemList",goods.getItemList());

        TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
        mv.addObject("itemCat1",itemCat1.getName());

        TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
        mv.addObject("itemCat2",itemCat2.getName());

        TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
        mv.addObject("itemCat3",itemCat3.getName());

        return mv;
    }
}
