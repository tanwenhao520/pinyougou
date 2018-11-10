package com.tan.solr;

import com.alibaba.fastjson.JSON;
import com.tan.dao.ItemMapper;
import com.tan.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class ImportSolr {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void  importSolrData(){
        TbItem tbItem = new TbItem();
        tbItem.setStatus("1");

        List<TbItem> select = itemMapper.select(tbItem);

        for (TbItem item : select) {
            Map spec = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(spec);
        }
        solrTemplate.saveBeans(select);
        solrTemplate.commit();
    }

}
