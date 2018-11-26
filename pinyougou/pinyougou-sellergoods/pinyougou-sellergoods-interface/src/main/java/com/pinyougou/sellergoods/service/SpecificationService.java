package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;

public interface SpecificationService extends BaseService<TbSpecification> {

    //分页查询
    PageResult search(Integer page, Integer rows, TbSpecification specification);

    //添加规格
    void add(Specification specification);

    //查找一个规格
    Specification findOne(Long id);

    //修改规格
    void update(Specification specification);

    //删除规格
    void deleteSpecificationByIds(Long[] ids);
}