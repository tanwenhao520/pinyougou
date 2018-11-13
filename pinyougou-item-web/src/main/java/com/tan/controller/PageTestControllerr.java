package com.tan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.pojo.TbItemCat;
import com.tan.sellergoods.service.GoodsService;
import com.tan.sellergoods.service.ItemCatService;
import com.tan.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/test")
@RestController
public class PageTestControllerr {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;


    @Reference
    private ItemCatService itemCatService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     *  审核商品后生成商品 html 页面到指定路径
     * @param goodsIds  商品 id 集合
     * @return
     */
    @GetMapping("/audit")
    public String audit(Long[] goodsIds){
        for (Long goodsId : goodsIds) {
            getHtml(goodsId);
        }
        return "success";
    }

    private void getHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            configuration.setDefaultEncoding("utf-8");
            Template template = configuration.getTemplate("item.ftl");
            Map<String,Object> dataModel = new HashMap<>();
            // 根据商品 id 查询商品基本、描述、启用的 SKU 列表
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");
            // 商品基本信息
            dataModel.put("goods", goods.getGoods());
            // 商品描述信息
            dataModel.put("goodsDesc", goods.getGoodsDesc());
            //商品sku信息
            dataModel.put("itemList",goods.getItemList());

            // 查询三级商品分类
            TbItemCat itemCat1 =
                    itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1", itemCat1.getName());
            TbItemCat itemCat2 =
                    itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2", itemCat2.getName());
            TbItemCat itemCat3 =
                    itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3", itemCat3.getName());

            String filename = ITEM_HTML_PATH+goodsId+".html";
            FileWriter fw = new FileWriter(filename);
            template.process(dataModel,fw);
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 删除静态资源
     * @param goodsIds
     * @return
     */
    @GetMapping("/delete")
    public  String delete(Long[] goodsIds){
        for (Long goodsId : goodsIds) {
           String filename = ITEM_HTML_PATH+goodsId+".html";
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
        return "success";
    }
}
