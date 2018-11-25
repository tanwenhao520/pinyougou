package com.tan.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tan.cart.CartService;
import com.tan.common.CookieUtils;
import com.tan.vo.Cart;
import com.tan.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {
    private static  final  String COOKIE_CART_LIST = "PYG_CART_LIST";

    private static final int COOKIE_CART_LIST_MAX_AGE = 3600*24;

    @Autowired
    private HttpServletRequest request;

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletResponse response;

    /**
     * 获取登录名
     * @return
     */
    @GetMapping("/getUsername")
    public Map<String,Object> getUsername(){
        Map<String,Object> map = new HashMap<>();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",name);
        return map;
    }

    @GetMapping("/findCartList")
    public List<Cart> findCartList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> cookieCartList = new ArrayList<>();
        String cartListJsonStr = CookieUtils.getCookieValue(request,COOKIE_CART_LIST,true);
        if(!StringUtils.isEmpty(cartListJsonStr)){
            cookieCartList = JSONArray.parseArray(cartListJsonStr,Cart.class);
        }
        if("anonymousUser".equals(name)){
            return cookieCartList;
        }else{
            List<Cart> redisCartList = cartService.findCartListByUserName(name);
            if(cookieCartList.size() > 0){
                redisCartList = cartService.mergeCartList(redisCartList,cookieCartList);
                cartService.saveCartListByUserName(redisCartList,name);
                CookieUtils.deleteCookie(request,response,COOKIE_CART_LIST);
            }
            return redisCartList;
        }

    }

    @GetMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://item.pinyougou.com",allowCredentials = "true")
    public Result addItemToCartList(Long itemId,Integer num){
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Cart> cartList = findCartList();
            List<Cart> newCartList =cartService.addItemToCartList(cartList,itemId,num);
            if("anonymousUser".equals(name)){
                String cartListJsonStr = JSON.toJSONString(newCartList);
                CookieUtils.setCookie(request,response,COOKIE_CART_LIST,cartListJsonStr,
                        COOKIE_CART_LIST_MAX_AGE,true);
            }else{
                 cartService.saveCartListByUserName(newCartList,name);
            }
            return Result.ok("加入购物车成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("加入购物车失败！");
    }
}
