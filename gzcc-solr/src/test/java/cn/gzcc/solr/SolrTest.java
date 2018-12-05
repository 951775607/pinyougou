package cn.gzcc.solr;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description solr中文搜索引擎测试
 * @Author Leon
 * @Date 2018/12/4 19:27
 * @Version 1.0
 **/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    
    /**
     * 功能描述:新增修改 
     *
     * @param: null
     * @return: null
     * @auther: Leon
     * @date: 2018/12/4 19:52
     **/
    @Test
    public void testAdd() {
        TbItem item = new TbItem();
        item.setId(1436707L);
        item.setTitle("222 JBL GO 音乐金砖 蓝牙音箱 低音炮 户外便携音响 迷你小音箱 可免提通话 魂动红");
        item.setBrand("JBL");
        item.setPrice(new BigDecimal(219));
        item.setImage("http://item.jd.com/1436707.html");
        item.setCategory("音箱");
        item.setStatus("1");

        //保存
        solrTemplate.saveBean(item);

        //提交
        solrTemplate.commit();
    }
    
    /**
     * 功能描述:根据id删除(根据主键查询)
     *
     * @param: Long id
     * @return: null
     * @auther: Leon
     * @date: 2018/12/4 19:52
     **/
    @Test
    public void deleteById() {
        solrTemplate.deleteById("1436707");

        //提交
        solrTemplate.commit();
    }

    /**
     * 功能描述:根据条件删除
     *
     * @param: null
     * @return: null
     * @auther: Leon
     * @date: 2018/12/4 19:56
     **/
    @Test
    public void deleteByQuery() {
        //创建查询对象
        SimpleQuery query = new SimpleQuery();

        //设置查询条件 contains不会分词
        Criteria criteria = new Criteria("item_title").contains("222");
        query.addCriteria(criteria);

        //删除
        solrTemplate.delete(query);

        //提交
        solrTemplate.commit();
    }

    /**
     * 功能描述:根据分页信息条件查询
     *
     * @param: []
     * @return: void
     * @auther: Leon
     * @date: 2018/12/4 20:14
     **/
    @Test
    public void queryForPage() {
        //创建分页查询对象
        SimpleQuery query = new SimpleQuery("item_title:jbl");
        //设置分页
        int page = 1;
        //分页起始索引号,相当于mysql limit  起始索引号，页大小
        //起始索引号 = （页号-1）*页大小
        query.setOffset(0);
        //页大小；默认为10
        query.setRows(20);
        /*
            参数1：查询对象
            参数2：查询返回结果对应的实体类（需要使用@Field）
         */
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage);
    }

    /**
     * 根据多条件查询
     */
    @Test
    public void multiQuery(){
        //创建查询对象query
        SimpleQuery query = new SimpleQuery();

        //设置查询条件 contains不会分词；多条件之间是并列的关系
        Criteria criteria = new Criteria("item_title").contains("jbl");
        query.addCriteria(criteria);

        Criteria criteria2 = new Criteria("item_price").greaterThanEqual(100);
        query.addCriteria(criteria2);

        /*
        参数1：查询对象
        参数2：查询返回结果对应的实体类（需要使用@Field）
         */
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage );
    }


    /**
     * 功能描述:输出查询信息
     *
     * @param: [scoredPage]
     * @return: void
     * @auther: Leon
     * @date: 2018/12/4 20:26
     **/
    private void showPage(ScoredPage<TbItem> scoredPage) {
        System.out.println("总记录数" + scoredPage.getTotalElements());
        System.out.println("总页数" + scoredPage.getTotalPages());

        List<TbItem> itemList = scoredPage.getContent();
        if (itemList != null && itemList.size() > 0) {
            for (TbItem tbItem : itemList) {
                System.out.println("id = " + tbItem.getId());
                System.out.println("title = " + tbItem.getTitle());
                System.out.println("price = " + tbItem.getPrice());
                System.out.println("image = " + tbItem.getImage());
                System.out.println("category = " + tbItem.getCategory());
                System.out.println("status = " + tbItem.getStatus());
            }
        }
    }

}
