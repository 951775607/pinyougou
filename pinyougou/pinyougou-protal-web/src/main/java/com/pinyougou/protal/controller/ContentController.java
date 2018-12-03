package com.pinyougou.protal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 门户页面显示controller
 * @Author Leon
 * @Date 2018/12/3 20:30
 * @Version 1.0
 **/
@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference(timeout = 10000)
    //创建广告详细信息业务层
    //从远程服务中获取返回结果的等待时间
    private ContentService contentService;

    /**
     * 加载内容分类id为1并且状态为有效的那些可以使用的广告数据降序排序
     *
     * @param categoryId 内容分类id
     * @return 内容列表
     */
    @GetMapping("/findContentListByCategoryId")
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        return contentService.findContentListByCategoryId(categoryId);
    }


}
