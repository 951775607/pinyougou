package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    //商品spu表
    private GoodsMapper goodsMapper;

    @Autowired
    //商品sku表
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    //商品规格表
    private ItemMapper itemMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        //1. 商家限定
        //1.1不查询已经标记删除的商品
        criteria.andNotEqualTo("isDelete", "1");

        //添加商家id
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId", goods.getSellerId());
        }
        //添加商品审核状态信息
        if (!StringUtils.isEmpty(goods.getAuditStatus())) {
            criteria.andEqualTo("auditStatus", goods.getAuditStatus());
        }
        //添加商品的名称模糊查询
        if (!StringUtils.isEmpty(goods.getGoodsName())) {
            criteria.andLike("goodsName", goods.getGoodsName());
        }


        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 商品的基本、描述、sku列表信息之后要保存到数据库中
     * @param goods 商品信息
     * @return 操作结果
     */
    @Override
    public void addGoods(Goods goods) {
        //新增商品基本信息
        goodsMapper.insertSelective(goods.getGoods());

        //新增商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        //保存商品sku列表（每个sku都要保存到tb_item）
        saveItemList(goods);
    }


    /**
     * 功能描述: 查找一个具体的商品信息
     *
     * @param: [id]
     * @return: com.pinyougou.vo.Goods
     * @auther: Leon
     * @date: 2018/12/1 16:38
     **/
    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();
        //查询商品spu
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        //封装商品基本信息
        goods.setGoods(tbGoods);

        //查询商品描述信息
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        //封装商品描述信息
        goods.setGoodsDesc(tbGoodsDesc);

        //查询商品的sku列表
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId", id);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        //封装商品sku信息
        goods.setItemList(itemList);

        return goods;
    }


    /**
     * 功能描述:修改商品信息
     *
     * @param: [goods]
     * @return: com.pinyougou.vo.Result
     * @auther: Leon
     * @date: 2018/12/1 17:07
     **/
    @Override
    public void updateGoods(Goods goods) {
        //更新商品的基本信息
        //修改过的话商品审核信息为未审核状态
        goods.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());

        //更新商品的描述信息（spu列表）
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());


        //删除原有的商品的sku列表
        TbItem param = new TbItem();
        //添加删除的商品id
        param.setGoodsId(goods.getGoods().getId());
        //删除sku表中的详细信息
        itemMapper.delete(param);

        //保存新的商品sku列表
        saveItemList(goods);
    }

    /**
     * 功能描述:根据商品的spu id数据更新商品的状态
     *
     * @param: ids 商品的spu id数组
     * @return:status 商品的状态
     * @auther: Leon
     * @date: 2018/12/1 17:23
     **/
    @Override
    public void updateStatus(Long[] ids, String status) {
        //update tb_goods set audit_status=1 where id  in (?,?,?)
        TbGoods goods = new TbGoods();
        goods.setAuditStatus(status);

        Example example = new Example(TbGoods.class);

        //设置查询条件；id
        example.createCriteria().andIn("id", Arrays.asList(ids));

        //参数1：更新对象，参数2：更新条件
        goodsMapper.updateByExampleSelective(goods, example);

        //如果审核通过则需要更新商品SKU的上架或者下加架，如果状态为1,已启用
        if ("2".equals(status)) {
            TbItem item = new TbItem();
            item.setStatus("1");

            Example example1 = new Example(TbItem.class);
            example.createCriteria().andIn("goodsId", Arrays.asList(ids));

            //update tb_item set status='1' where goods_id in(?,?....)
            //更新商品的上架信息
            itemMapper.updateByExampleSelective(item, example);
        }
    }

    /**
     * 功能描述:保存动态sku列表
     *
     * @param: goods 商品信息（基本、描述、sku列表）
     * @return: null
     * @auther: Leon
     * @date: 2018/11/30 20:16
     **/
    private void saveItemList(Goods goods) {
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            //如果启用规格，则需要按照规格生成不同的sku商品
            if (goods.getItemList() != null && goods.getItemList().size() > 0) {
                for (TbItem item : goods.getItemList()) {
                    //标题规格选项形成sku标题=spu名称+所有规格的选项值
                    String title = goods.getGoods().getGoodsName();
                    //获取规格；{"网络":"移动3G","机身内存":"16G"}
                    Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
                    Set<Map.Entry<String, String>> entries = map.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        title += " " + entry.getValue();
                    }
                    item.setTitle(title);

                    setItemValue(item, goods);

                    //保存sku
                    itemMapper.insertSelective(item);
                }
            }
        } else {
            //如果未启用规格，则只存在一条sku信息
            //创建item对象：大多数数据来自spu设置到对象中
            TbItem tbItem = new TbItem();
            //如果spu中没有的数据，如：spec（｛｝），num（9999），status(0未启用)，isDefault(1默认)
            tbItem.setSpec("{}");
            //设置价格
            tbItem.setPrice(goods.getGoods().getPrice());
            //设置字段
            tbItem.setStatus("0");
            //设置数量
            tbItem.setNum(9999);
            //设置是否默认
            tbItem.setIsDefault("1");
            //设置标题
            tbItem.setTitle(goods.getGoods().getGoodsName());
            //设置商品的其他信息
            setItemValue(tbItem, goods);
            //保存到数据库中
            itemMapper.insertSelective(tbItem);
        }
    }

    /**
     * 功能描述:保存商品的所有信息
     *
     * @param: TbItem item,Goods goods
     * @return: null
     * @auther: Leon
     * @date: 2018/11/30 20:57
     **/
    private void setItemValue(TbItem item, Goods goods) {
        //商品分类id 来自 商品spu的第3级商品分类id
        item.setCategoryid(goods.getGoods().getCategory3Id());
        //根据商品分类id查询商品sku
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
        item.setCategory(itemCat.getName());

        //保存图片
        if (!StringUtils.isEmpty(goods.getGoodsDesc().getItemImages())) {
            List<Map> imageList = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
            //获取到图片路径不为空的话保存图片
            if (imageList.get(0).get("url") != null) {
                item.setImage(imageList.get(0).get("url").toString());
            }
        }

        //保存商品基本信息id
        item.setGoodsId(goods.getGoods().getId());
        //品牌
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());

        //保存添加时间
        item.setCreateTime(new Date());

        //保存更新时间
        item.setUpdateTime(item.getCreateTime());

        //卖家信息
        item.setSellerId(goods.getGoods().getSellerId());
        TbSeller seller = sellerMapper.selectByPrimaryKey(item.getSellerId());
        item.setSeller(seller.getName());
    }

    //根据商品spu_id更新商品的删除状态
    public void deleteGoodsByIds(Long[] ids) {
        TbGoods goods = new TbGoods();
        //设置商品删除状态，删除为1，未删除为0
        goods.setIsDelete("1");
        Example example = new Example(TbGoods.class);
        //Example.Criteria criteria = example.createCriteria();
        //criteria.andIn("id", Arrays.asList(ids));
        //添加商品删除信息
        example.createCriteria().andIn("id", Arrays.asList(ids));
        //修改商品基本信息表
        goodsMapper.updateByExampleSelective(goods, example);
    }



    
}
