package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Date:2018/11/22
 * Author:Leon
 * Desc
 */
public interface BrandMapper extends Mapper<TbBrand> {
    public List<TbBrand> queryAll();
}
