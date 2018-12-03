package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContentCategory;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

/**
 * 功能描述:广告分类信息
 **/
public interface ContentCategoryService extends BaseService<TbContentCategory> {

    PageResult search(Integer page, Integer rows, TbContentCategory contentCategory);
}