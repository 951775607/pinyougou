package com.pinyougou.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;

/**
 * Date:2018/11/24
 * Author:Leon
 * Desc
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {
    //spring 4.x 版本之后引入的泛型依赖注入
    @Autowired
    private Mapper<T> mapper;

    public T findOne(Serializable id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<T> findAll() {
        return mapper.selectAll();
    }

    public List<T> findByWhere(T t) {
        return mapper.select(t);
    }

    public PageResult findPage(Integer page, Integer pageSize) {
        //设置分页
        PageHelper.startPage(page, pageSize);

        //查询
        List<T> list = mapper.selectAll();

        //创建分页信息对象
        PageInfo<T> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageResult findPage(Integer page, Integer pageSize, T t) {
        //设置分页
        PageHelper.startPage(page, pageSize);

        //查询
        List<T> list = mapper.select(t);

        //创建分页信息对象
        PageInfo<T> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    public void add(T t) {
        //选择性新增：如果对象中没有设置值的那些属性，则不会在操作语句中出现
        //如如果只给name：insert into tb_brand(name) values(?)
        //如如果只给name,firstChar：insert into tb_brand(name, first_char) values(?,?)
        mapper.insertSelective(t);
    }


    public void update(T t) {
        //选择性更新：如果对象中没有设置值的那些属性，则不会在操作语句中出现
        //如如果只给id, name：update tb_brand set name =? where id=?
        //如如果只给id, name,firstChar：update tb_brand set name =?,first_char=? where id=?
        mapper.updateByPrimaryKeySelective(t);

    }


    public void deleteByIds(Serializable[] ids) {
        if(ids != null && ids.length > 0){
            for (Serializable id : ids) {
                mapper.deleteByPrimaryKey(id);
            }
        }
    }

}
