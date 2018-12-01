package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);

    /**
     * 功能描述: 根据分类模板id查询其对应的规格的选项
     *
     * @param: id 分类模板id
     * @return: List<Map>
     * @auther: Leon
     * @date: 2018/11/30 19:39
     **/
    List<Map> findSpecList(Long id);
}