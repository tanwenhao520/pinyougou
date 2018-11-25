package com.tan.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.pojo.TbItemCat;
import com.tan.sellergoods.service.GoodsService;
import com.tan.sellergoods.service.ItemCatService;
import com.tan.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ItemAuditMessageListener extends AbstractAdaptableMessageListener {

    @Value("${ITEM_HTML_PATH}")
    private  String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage)message;
        Long[] ids=  (Long[])objectMessage.getObject();
        for (Long id : ids) {
            genItemHtml(id);
        }
        System.out.println("同步生成静态页面完成！");
        
    }

    /**
     * 生成静态页面
     * @param id
     */
    private void genItemHtml(Long id) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            Map<String,Object> dataModel = new HashMap<>();
            Goods goods = goodsService.findGoodsByIdAndStatus(id, "1");
            dataModel.put("goods",goods.getGoods());
            dataModel.put("goodsDesc",goods.getGoodsDesc());
            TbItemCat one = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1",one.getName());
            TbItemCat two = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2",two);
            TbItemCat three = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3",three);
            dataModel.put("itemList",goods.getItemList());

            String filename = ITEM_HTML_PATH+id+".html";
            FileWriter fw = new FileWriter(filename);
            template.process(dataModel,fw);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
