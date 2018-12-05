package com.pinyougou.solr;

import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author Leon
 * @Date 2018/12/5 9:04
 * @Version 1.0
 **/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class ItemImport2SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void test() {
        //获取已经通过审核的商品sku列表
        TbItem parm = new TbItem();
        //已经启用
        parm.setStatus("1");
        List<TbItem> itemList = itemMapper.select(parm);

        //转换每一个sku中的spec到specMap
        for (TbItem tbItem : itemList) {
            System.out.println(tbItem.getGoodsId());
            Map map = JSONObject.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(map);
        }

        //保存sku列表
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();


    }



}
