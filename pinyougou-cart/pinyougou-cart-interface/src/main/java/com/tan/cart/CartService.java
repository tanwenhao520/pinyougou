package com.tan.cart;

import com.tan.vo.Cart;

import java.util.List;

public interface CartService {

    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    List<Cart> findCartListByUserName(String name);

    void saveCartListByUserName(List<Cart> newCartList, String name);

    List<Cart> mergeCartList(List<Cart> cartList, List<Cart> cartList2);
}
