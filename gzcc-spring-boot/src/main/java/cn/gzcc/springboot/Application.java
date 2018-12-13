package cn.gzcc.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description TODO
 * @Date 2018/12/13 17:19
 * @Version 1.0
 **/

/**
 * 如果是 spring boot 的引导类，需要添加@SpringBootApplication 注解
 * 默认将扫描该引导类及其子包里面的 spring 注解
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
