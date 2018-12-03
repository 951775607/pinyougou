package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * 功能描述:广告详细信息页面
 * @auther: Leon
 * @date: 2018/12/3 20:24
 **/
@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 功能描述: 加载内容分类id为1并且状态为有效的那些可以使用的广告数据降序排序
     *
     * @param:categoryId 内容分类id
     * @return: 内容列表
     * @auther: Leon
     * @date: 2018/12/3 20:22
     **/
    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        //启用状态信息
        criteria.andEqualTo("status", "1");
        //降序排序
        example.orderBy("sortOrder").desc();
        List<TbContent> list = contentMapper.selectByExample(example);
        return list;
    }

}
