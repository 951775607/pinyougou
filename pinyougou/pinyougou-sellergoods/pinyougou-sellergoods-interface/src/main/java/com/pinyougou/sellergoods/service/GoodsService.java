package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    /**
     * 商品的基本、描述、sku列表信息之后要保存到数据库中
     * @param goods 商品信息
     * @return 操作结果
     */
    void addGoods(Goods goods);

    /**
     * 功能描述: 查找一个具体的商品信息
     *
     * @param: [id]
     * @return: com.pinyougou.vo.Goods
     * @auther: Leon
     * @date: 2018/12/1 16:38
     **/
    Goods findGoodsById(Long id);


    /**
     * 功能描述: 修改商品信息
     *
     * @param: [goods]
     * @return: com.pinyougou.vo.Result
     * @auther: Leon
     * @date: 2018/12/1 17:07
     **/
    void updateGoods(Goods goods);

    /**
     * 功能描述:根据商品的spu id数据更新商品的状态
     *
     * @param: ids 商品的spu id数组
     * @return:status 商品的状态
     * @auther: Leon
     * @date: 2018/12/1 17:23
     **/
    void updateStatus(Long[] ids, String status);

    /**
     * 功能描述: 运营商后台-商品管理【商品删除】
     *
     * @param: [ids]
     * @return: com.pinyougou.vo.Result
     * @auther: Leon
     * @date: 2018/12/1 18:45
     **/
    void deleteGoodsByIds(Long[] ids);

    /**
     * 功能描述:实现商品的上架与下架功能
     *
     * @param:ids,statues
     * @return:Result
     * @auther: Leon
     * @date: 2018/12/1 19:39
     **/
     void updateMarketable(Long[] ids, String status);

     /**
      * 功能描述:根据商品SPU id集合和状态查询这些商品对应的sku商品列表
      *
      * @param: ids 商品spu id集合
        status sku商品状态
      * @return: sku商品列表
      * @date: 2018/12/7 20:58
      **/
    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String s);
}