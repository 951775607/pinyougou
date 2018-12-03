package cn.gzcc.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis-cluster.xml")
public class RedisClusterTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 测试字符串
     */
    @Test
    public void testString(){
        redisTemplate.boundValueOps("string_key").set("i_am_ljb can we chat gzcc");
        Object obj = redisTemplate.boundValueOps("string_key").get();
        System.out.println(obj);
    }

    /**
     * 测试散列
     */
    @Test
    public void testHash(){
        redisTemplate.boundHashOps("h_key").put("f1", "a");
        redisTemplate.boundHashOps("h_key").put("f2", "b");
        redisTemplate.boundHashOps("h_key").put("f3", "c");
        //获取所有域对应的值
        Object obj = redisTemplate.boundHashOps("h_key").values();
        System.out.println(obj);
    }

    /**
     * 测试列表
     */
    @Test
    public void testList(){
        redisTemplate.boundListOps("l_key").rightPush("b");
        redisTemplate.boundListOps("l_key").rightPush("c");
        redisTemplate.boundListOps("l_key").leftPush("a");
        //起始索引号，结束索引号（如果为-1表示最后一个索引号）
        Object obj = redisTemplate.boundListOps("l_key").range(0, -1);
        System.out.println(obj);
    }

    /**
     * 测试集合
     */
    @Test
    public void testSet(){
        redisTemplate.boundSetOps("set_key").add("a", "b", "c");
        Object obj = redisTemplate.boundSetOps("set_key").members();
        System.out.println(obj);
    }

    /**
     * 测试有序集合会自动按照分值升序排序元素
     */
    @Test
    public void testZSet(){
        redisTemplate.boundZSetOps("z_key").add("c", 10);
        redisTemplate.boundZSetOps("z_key").add("a", 25);
        redisTemplate.boundZSetOps("z_key").add("b", 20);
        Object obj = redisTemplate.boundZSetOps("z_key").range(0, -1);
        System.out.println(obj);
    }

}
