package com.tan.test;

import com.tan.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-solr.xml")
public class SolrTest {
    @Autowired
    private  SolrTemplate solrTemplate;

    @Test
    public void addAndUpdate(){
        TbItem tbItem = new TbItem();
        tbItem.setId(1L);
        tbItem.setTitle("苹果手机|华为手机|华为|小米|手机自营|苹果|oppo|魅族|vivo|小米8|二手手机|手机oppo|");
        tbItem.setBrand("中兴");
        tbItem.setPrice(new BigDecimal(100));
        solrTemplate.saveBean(tbItem);
    }

    @Test
    public  void delete(){
        SimpleQuery sq = new SimpleQuery("*:*");
        solrTemplate.delete(sq);
        solrTemplate.commit();
    }

    @Test
    public void select(){
        SimpleQuery sq = new SimpleQuery("*:*");
        sq.setOffset(0);
        sq.setRows(10);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(sq, TbItem.class);
        showPage(tbItems);
    }

    private void showPage(ScoredPage<TbItem> tbItems) {
        for (TbItem tbItem : tbItems) {
            System.out.println("id "+tbItem.getId());
            System.out.println("品牌 "+tbItem.getBrand());
        }
    }

    @Test
    public void selectByQuery(){
        SimpleQuery sq = new SimpleQuery();
        Criteria contains = new Criteria("item_title").contains("三星");
        Criteria contains1 = new Criteria("item_price").greaterThan(5000);
        sq.addCriteria(contains);
        sq.addCriteria(contains1);

        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(sq, TbItem.class);

        System.out.println(tbItems.getContent().size());

        showPage(tbItems);
        }
}
