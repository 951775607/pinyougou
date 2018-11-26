package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * Date:2018/11/22
 * Author:Leon
 * Desc
 */
public interface BrandService extends BaseService<TbBrand> {
    /**
     * 查询所有品牌数据
     * @return 品牌列表
     */
    @Deprecated
    public List<TbBrand> queryAll();

    /**
     * 根据页号和页大小查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @Deprecated
    List<TbBrand> testPage(Integer page, Integer rows);

    PageResult search(TbBrand tbBrand,Integer page, Integer pageSize);

    /**
     * 查询品牌列表
     * @return 品牌列表,数据结构如：[{"id":1,"text":"联想"},{"id":2,"text":"华为"}]
     */
    List<Map> selectOptionList();
}
