package com.tan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.dao.*;
import com.tan.pojo.*;
import com.tan.sellergoods.service.GoodsService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.Goods;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
@Transactional
@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

            criteria.andNotEqualTo("isDelete","1");

        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId", goods.getSellerId() );
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus", goods.getAuditStatus() );
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%"+goods.getGoodsName()+"%" );
        }
        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


    @Override
    public void add(Goods goods) {
        goodsMapper.insertSelective(goods.getGoods());
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
    }



    /**
     *重写父类add方法，再增加(SPU)商品规格表的同时增加(SPU)商品描述表、最后再增加(SKU)商品表
     * @param goods
     */
    @Override
    public void addGoods(Goods goods) {
        goodsMapper.insertSelective(goods.getGoods());

        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());

        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        saveItem(goods);

        }

    @Override
    public void updateStatus(Long[] ids,String status) {
        if(ids.length > 0 ){

            TbGoods tg = new TbGoods();
            tg.setAuditStatus(status);

            Example example = new Example(TbGoods.class);

            example.createCriteria().andIn("id", Arrays.asList(ids));

            goodsMapper.updateByExampleSelective(tg,example);

            if("2".equals(status)){
                TbItem tbItem = new TbItem();
                tbItem.setStatus("1");
                Example example1 = new Example(TbItem.class);
                example1.createCriteria().andIn("goodsId",Arrays.asList(ids));
                itemMapper.updateByExampleSelective(tbItem,example1);
            }
        }

    }

    @Override
    public Goods findGoodsById(Long id) {

        Goods goods = new Goods();

        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        System.out.println(tbGoods);

        goods.setGoods(tbGoods);

        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goods.getGoods().getId());

        goods.setGoodsDesc(tbGoodsDesc);
        System.out.println(tbGoodsDesc);
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId",goods.getGoodsDesc().getGoodsId());
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        System.out.println(tbItems);
        goods.setItemList(tbItems);

        return goods;


       /* Example example1 = new Example(TbContent.class);
        Example.Criteria criteria = example1.createCriteria();
        criteria.andEqualTo("status","1");
        criteria.andEqualTo("categoryId","1");
        example1.orderBy("sortOrder").desc();*/


    }

    /**
     *
     * @param goods
     */
    @Override
    public void update(Goods goods) {

        goods.getGoods().setAuditStatus("0");

        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());

        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        TbItem tb = new TbItem();

        tb.setGoodsId(goods.getGoodsDesc().getGoodsId());

        itemMapper.delete(tb);

        saveItem(goods);
    }


    /**
     *逻辑删除商品，只改状态
     * @param ids
     */
    @Override
    public void deleteGoodsByIds(Long[] ids) {
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete("1");

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);

    }

    /**
     * 上架、下架 (暂无搞定)
     * @param ids
     * @param status
     */
    @Override
    public void updateis_markeTable(Long[] ids, String status)  {

        List<TbGoods> tbGoods1 = goodsMapper.selectByExample(ids);

        Map<String,String> map = new HashMap<>();

        for (int i = 0; i < tbGoods1.size(); i++){
            if("2".equals(tbGoods1.get(i).getIsMarketable())){
                map.put("status",tbGoods1.get(i).getAuditStatus());
            }else{
                map.put("status",tbGoods1.get(i).getAuditStatus());
                break;
            }
        }
        for (String s : map.values()) {
            if("2".equals(s)){
                    TbItem tbItem = new TbItem();
                    TbGoods tbGoods = new TbGoods();
                    Example example = new Example(TbGoods.class);
                    example.createCriteria().andIn("id",Arrays.asList(ids));
                    if("2".equals(status)){
                        tbGoods.setIsMarketable("2");
                        tbItem.setStatus("2");
                    }
                    if("1".equals(status)){
                        tbGoods.setIsMarketable("1");
                        tbItem.setStatus("1");
                    }
                    Example example2 = new Example(TbItem.class);
                    example.createCriteria().andIn("goodsId",Arrays.asList(ids));
                    goodsMapper.updateByExampleSelective(tbGoods,example);

                    itemMapper.updateByExampleSelective(tbItem,example2);
            }else{

            }
        }



    }

    /**
     * 按spu商品ids 和 状态为 2(以审核)的商品查询 sku商品，用于给Solr新增索引
     * @param ids
     * @param status
     */
    @Override
    public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status) {
        Example example = new Example(TbItem.class);
        example.createCriteria().andIn("goodsid",Arrays.asList(ids)).andEqualTo("status",status);
        return  itemMapper.selectByExample(example);
    }

    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String status) {

            Goods goods = new Goods();

            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            goods.setGoods(tbGoods);

            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            goods.setGoodsDesc(tbGoodsDesc);

            Example example = new Example(TbItem.class);
            example.createCriteria().andEqualTo("goodsId",goodsId).andEqualTo("status",status);
            example.orderBy("isDefault").desc();
            List<TbItem> tbItems = itemMapper.selectByExample(example);
            goods.setItemList(tbItems);


        return goods;
    }

    /**
     * 增加(SKU)商品表，如果点击了选定规格时才添加，否则只添加一条默认数据
     * @param goods
     */
    private void  saveItem(Goods goods){
        if("1".equals(goods.getGoods().getIsEnableSpec())){
            for (TbItem tbItem : goods.getItemList()) {
                String title = goods.getGoods().getGoodsName();
                Map<String,Object> map = JSON.parseObject(tbItem.getSpec());

                Set<Map.Entry<String, Object>> entries = map.entrySet();

                for (Map.Entry<String, Object> entry : entries) {
                    title += " "+entry.getValue();
                }

                tbItem.setTitle(title);

                setItemValue(tbItem,goods);

                itemMapper.insert(tbItem);
             }

        }else{
            TbItem tb = new TbItem();
            tb.setTitle(goods.getGoods().getGoodsName());
            tb.setPrice(goods.getGoods().getPrice());
            tb.setNum(9999);
            tb.setStatus("0");
            tb.setIsDefault("1");
            tb.setSpec("{}");

            setItemValue(tb,goods);

            itemMapper.insert(tb);
        }


    }

    /**
     * 处理新增时前端传过来的值
     * @param tbItem
     * @param goods
     */
    private void  setItemValue(TbItem tbItem,Goods goods){

        List<Map>  arr = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);

        if(arr != null && arr.size() > 0){
            tbItem.setImage(arr.get(0).get("url").toString());
        }

        tbItem.setCategoryid(goods.getGoods().getCategory3Id());

        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());


        tbItem.setCategory(tbItemCat.getName());


        tbItem.setGoodsId(goods.getGoods().getId());

        tbItem.setCreateTime(new Date());

        tbItem.setUpdateTime(tbItem.getCreateTime());

        tbItem.setSellerId(goods.getGoods().getSellerId());

        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());

        tbItem.setSeller(seller.getName());

        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());

        tbItem.setBrand(tbBrand.getName());
    }

}
