package cn.gzcc.freemarker;

import freemarker.template.Configuration;
import org.junit.Test;

import java.io.FileWriter;
import java.util.HashMap;


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
        configuration.getTemplate("test.ftl");
        //5. 获取数据
        HashMap<String, Object> dataModel = new HashMap<>();
        dataModel.put("name", "广州商学院");
        dataModel.put("message", "欢迎使用Freemarker");
        //6. 创建一个文件编写对象Writer
        FileWriter fileWriter = new FileWriter("");
        //7. 使用模版和数据输出到指定路径
        //8. 关闭资源
    }
}
