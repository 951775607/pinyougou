package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
//import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.*;

import javax.jms.*;

import java.util.List;


/**
 * 商品基本信息
 * */
@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;


    //使用了mq，itemSearchService从这里解耦，更新solr的通知发送到mq
    //然后itemSearchService也从mq获取消息,实现解耦操作
    // @Reference
//     private ItemSearchService itemSearchService;



    @Autowired
    private JmsTemplate jmsTemplate;

    //修改操作
    @Autowired
    private ActiveMQQueue itemSolrQueue;

    //删除操作
    @Autowired
    private ActiveMQQueue itemSolrDeleteQueue;

    //实现页面静态华
    @Autowired
    private ActiveMQTopic itemTopic;

    @Autowired
    private ActiveMQTopic itemDeleteTopic;





    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbGoods goods) {
        try {
            goodsService.add(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbGoods findOne(Long id) {
        return goodsService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbGoods goods) {
        try {
            goodsService.update(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 更新商品的删除状态为已删除
     * @param ids 要删除的商品spu id数组
     * @return 操作结果
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);


            //更新搜索系统数据
            //通过mq来实现消息通知，实现解耦，不用在这里更新索引，使用下面的方式来更新
            //删除solr中对应商品索引数据
//            itemSearchService.deleteItemByGoodsIdList(Arrays.asList(ids));
            sendMQMsg(itemSolrDeleteQueue, ids);

            //发送商品删除的订阅信息
            sendMQMsg(itemDeleteTopic, ids);

            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }


    /**
     * 功能描述:发送消息到ActiveMQ
     *
     * @param: destination 发送模式
     * @param： ids 商品id集合
     * @date: 2018/12/11 16:07
     **/
    private void sendMQMsg(Destination destination, Long[] ids) {
        try {
            jmsTemplate.send(destination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage objectMessage = session.createObjectMessage();
                    objectMessage.setObject(ids);
                    return objectMessage;
                }
            });
        } catch (JmsException e) {
            e.printStackTrace();
        }

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
        return goodsService.search(page, rows, goods);
    }

    /**
     * 根据商品spu id数组更新那些商品的状态
     * @param ids 商品spu id数组
     * @param status 商品的状态
     * @return 操作结果
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try {
            goodsService.updateStatus(ids, status);
            if ("2".equals(status)) {
                //如果审核通过则需要更新solr索引库数据
                //查询到需要更新的商品列表
                List<TbItem> itemList = goodsService.findItemListByGoodsIdsAndStatus(ids, "1");

                //更新搜索系统数据
                //通过mq来实现消息通知，实现解耦，不用在这里更新索引，使用下面的方式来更新
//                itemSearchService.importItemList(itemList);
                /**
                 * 功能描述:发送通知到mq，搜索服务在mq查询到通知执行更新操作
                 *
                 * @param: [ids, status]
                 * @return: com.pinyougou.vo.Result
                 * @date: 2018/12/10 21:28
                 **/
                jmsTemplate.send(itemSolrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage();
                        textMessage.setText(JSON.toJSONString(itemList));
                        return textMessage;
                    }
                });
                //发送商品审核通过的订阅消息
                sendMQMsg(itemTopic, ids);

            }

            return Result.ok("更新商品状态成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新商品状态失败");
    }

}
