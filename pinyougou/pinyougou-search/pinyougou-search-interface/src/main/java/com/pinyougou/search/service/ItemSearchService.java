package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据搜索条件搜索商品
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    Map<String, Object> search(Map<String, Object> searchMap);


    /**
     * 功能描述:
     *
     * @param: 根据关键字搜索商品列表
     * @return: 搜索结果
     * @date: 2018/12/7 21:01
     **/


    /**
     * 功能描述:批量导入商品列表到solr索引库
     *
     * @param: 根据关键字搜索商品列表
     * @return: 搜索结果
     * @date: 2018/12/7 21:01
     **/
    void importItemList(List<TbItem> itemList);


    /**
     * 功能描述:根据goodsId商品id集合删除其对应在solr中的商品数据
     *
     * @param: goodsId商品id集合
     * @return:
     * @date: 2018/12/7 21:08
     **/
    void deleteItemByGoodsIdList(List<Long> goodsIdList);
}
