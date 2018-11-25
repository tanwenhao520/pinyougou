package com.tan.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.tan.cart.CartService;
import com.tan.dao.ItemMapper;
import com.tan.pojo.TbItem;
import com.tan.pojo.TbOrderItem;
import com.tan.vo.Cart;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {

    private static final String REDIS_CART_LIST ="CART_LIST";



    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {

        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        Logger.getLogger(CartServiceImpl.class).debug(tbItem);
        if(tbItem == null){
           throw new RuntimeException("商品不存在");
        }
        if(!"1".equals(tbItem.getStatus())){
            throw  new RuntimeException("商品状态不合法");
        }

        String sellerId = tbItem.getSellerId();
        Cart cart = findCartBySellerId(cartList,sellerId);
        Logger.getLogger(CartServiceImpl.class).debug(cart);
        if(cart == null){
            if(num > 0 ){
                Cart cart1 = new Cart();
                cart1.setSellerId(sellerId);
                cart1.setSellerName(tbItem.getSeller());

                List<TbOrderItem> list = new ArrayList<>();
                TbOrderItem tbOrderItem = createOrder(tbItem,num);
                list.add(tbOrderItem);
                cart1.setOrderItemList(list);
                cartList.add(cart1);

            }else{
                throw new RuntimeException("购买的数量不合法");
            }
        }else{
            TbOrderItem tbOrderItem = findOrderItemByItemId(cart.getOrderItemList(),itemId);
            if(tbOrderItem != null ){
                tbOrderItem.setNum(tbOrderItem.getNum() + num);
                tbOrderItem.setTotalFee(new BigDecimal( tbOrderItem.getPrice().doubleValue()*tbOrderItem.getNum()));
                if(tbOrderItem.getNum() <= 0 ){
                    cart.getOrderItemList().remove(tbOrderItem);
                }
                if(cart.getOrderItemList().size() <= 0 ){
                    cartList.remove(cart);
                }
            }else{
                if(num > 0){
                    tbOrderItem = createOrder(tbItem,num);
                    cart.getOrderItemList().add(tbOrderItem);
                }else{
                    throw  new RuntimeException("购买的数量不合法");
                }
            }
        }

        return cartList;
    }

    @Override
    public List<Cart> findCartListByUserName(String name) {
        List<Cart> list  = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(name);
        if(list != null && list.size() >0){
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public void saveCartListByUserName(List<Cart> newCartList, String name) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(name,newCartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList, List<Cart> cartList2) {
        for (Cart cart : cartList) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem tbOrderItem : orderItemList) {
                addItemToCartList(cartList2,tbOrderItem.getItemId(),tbOrderItem.getNum());
            }
        }
        return cartList2;
    }

    private TbOrderItem findOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        if(orderItemList.size() > 0 && orderItemList != null){
            for (TbOrderItem tbOrderItem : orderItemList) {
                if(itemId.equals(tbOrderItem.getItemId())){
                    return tbOrderItem;
                }
            }
        }
        return null;
    }

    private TbOrderItem createOrder(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setNum(num);
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        if(cartList != null && cartList.size() > 0){
            for (Cart cart : cartList) {
                if(sellerId.equals(cart.getSellerId())){
                 return cart;
                }
            }
        }
        return null;
    }
}
