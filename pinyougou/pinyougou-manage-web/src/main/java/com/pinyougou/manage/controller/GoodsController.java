package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商品基本信息
 * */
@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 商品的基本、描述、sku列表信息之后要保存到数据库中
     * @param goods 商品信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //设置商家信息
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            //设置商品id
            goods.getGoods().setSellerId(sellerId);
            //设置商品的审核状态
            goods.getGoods().setAuditStatus("0");

//            goodsService.add(goods);
            //添加商品
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 功能描述: 查找一个具体的商品信息
     *
     * @param: [id]
     * @return: com.pinyougou.vo.Goods
     * @auther: Leon
     * @date: 2018/12/1 16:38
     **/
    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    /**
     * 功能描述: 更改商品信息
     *
     * @param: [goods]
     * @return: com.pinyougou.vo.Result
     * @auther: Leon
     * @date: 2018/12/1 17:07
     **/
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            //校验商家是否有权限修改，获取这个商品的商家
            TbGoods oldGoods = goodsService.findOne(goods.getGoods().getId());
            //获取当前登录的商家
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (sellerId.equals(oldGoods.getSellerId()) && sellerId.equals(goods.getGoods().getSellerId())) {
                goodsService.updateGoods(goods);
            } else {
                return Result.fail("非法操作");
            }
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                             @RequestParam(value = "rows", defaultValue = "10")Integer rows) {

        //运营商不需要限定审核权限，可以查看所有商品
        return goodsService.search(page, rows, goods);
    }

    /**
     * 功能描述:根据商品的spu id数据更新商品的状态
     *
     * @param: ids 商品的spu id数组
     * @return:status 商品的状态
     * @auther: Leon
     * @date: 2018/12/1 17:23
     **/
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {

        try {
            //更新商品基本信息表中的状态信息
            goodsService.updateStatus(ids, status);
            return Result.ok("更新商品状态成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新商品状态失败");
    }

}
