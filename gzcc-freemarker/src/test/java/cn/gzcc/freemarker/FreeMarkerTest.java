package cn.gzcc.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.FileWriter;
import java.util.*;


/**
 * @Description freemarker测试项目
 * @Date 2018/12/8 18:44
 * @Version 1.0
 **/
public class FreeMarkerTest {
    @Test
    public void test() throws Exception {
        //1. 创建Configuration对象指定Freemarker版本；
        Configuration configuration = new Configuration(Configuration.getVersion());
        //2. 设置模版路径；
        configuration.setClassForTemplateLoading(FreeMarkerTest.class, "/ftl");
        //3. 指定生成文件的编码为utf-8；
        configuration.setDefaultEncoding("utf-8");
        //4. 获取模版
        Template template = configuration.getTemplate("test.ftl");
        //5. 获取数据
        HashMap<String, Object> dataModel = new HashMap<>();
        dataModel.put("name", "广州商学院");
        dataModel.put("message", "欢迎使用Freemarker");

        //创建一个集合对象
        List<Map<String, Object>> goodsList = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "橘子");
        map1.put("price", 4.5);
        goodsList.add(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "柚子");
        map2.put("price", 7);
        goodsList.add(map2);

        dataModel.put("goodsList", goodsList);

        dataModel.put("today", new Date());

        dataModel.put("number", 123456789L);


        //6. 创建一个文件编写对象Writer
        FileWriter fileWriter = new FileWriter("F:\\JavaCode\\mypyg\\test\\test.html");
        //7. 使用模版和数据输出到指定路径
        template.process(dataModel, fileWriter);
        //8. 关闭资源
        fileWriter.close();

    }
}
