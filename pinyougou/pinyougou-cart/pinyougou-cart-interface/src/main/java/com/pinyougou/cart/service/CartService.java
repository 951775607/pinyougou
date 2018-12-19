package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

/**
 * @Description 购物车接口
 * @Date 2018/12/18 19:30
 * @Version 1.0
 **/
public interface CartService {
    /**
     * 根据商品 id 查询商品和购买数量加入到 cartList
     *
     * @param cartList 购物车列表
     * @param itemId   商品 id
     * @param num      购买数量
     * @return 购物车列表
     */
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 功能描述:根据商品id查询商品和购买数量加入到cartList
     *
     * @param: cartList 购物车列表
     * @param: itemId商品id
     * @param: num购买数量
     * @return: 购物车列表
     **/
    List<Cart> findCartListByUsername(String userName);


    /**
     * 功能描述:将用户对应的购物车列表保存到redis中
     *
     * @param: 购物车列表
     * @param: username用户id
     **/
    void saveCartListByUsername(List<Cart> newCartList, String userName);


    /**
     * 功能描述:合并两个购物车
     *
     * @param: cartList1 购物车列表1
     * @param: cartList2 购物车列表2
     * @return: 合并后的购物车列表
     **/
    List<Cart> margeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
