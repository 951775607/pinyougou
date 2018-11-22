package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Date:2018/11/22
 * Author:Leon
 * Desc
 */
@RequestMapping("/brand")
@RestController
public class BrandController {
    //引入远程的服务对象
    @Reference
    private BrandService brandService;

    /**
     * 查询所有品牌数据
     * @return 品牌列表json格式字符串
     */
    /*@RequestMapping(value="/findAll", method = RequestMethod.GET)
    @ResponseBody*/
    @GetMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.queryAll();
    }
}
