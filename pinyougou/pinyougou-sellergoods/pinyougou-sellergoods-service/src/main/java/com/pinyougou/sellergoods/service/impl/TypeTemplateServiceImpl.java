package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service(interfaceClass = TypeTemplateService.class)
public class TypeTemplateServiceImpl extends BaseServiceImpl<TbTypeTemplate> implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(typeTemplate.getName())){
            criteria.andLike("name", "%" + typeTemplate.getName() + "%");
        }

        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 功能描述: 根据分类模板id查询其对应的规格的选项
     *
     * @param: id 分类模板id
     * @return: List<Map>
     * @auther: Leon
     * @date: 2018/11/30 19:39
     **/
    @Override
    public List<Map> findSpecList(Long id) {
        //1.根据分类模板id查询分类模板
        TbTypeTemplate typeTemplate = findOne(id);

        if (!StringUtils.isEmpty(typeTemplate.getSpecIds())) {
            //2.根据分类模板中的规格列表的每一个规格查询其对应的规格选项列表
            //规格列表[{"id":33,"text":"电视屏幕尺寸"}]
            //把数据库中的字符串数据转换成Map数据存储
            List<Map> specList = JSONArray.parseArray(typeTemplate.getSpecIds(), Map.class);
            for (Map map : specList) {
                //根据规格id查询规格选项
                TbSpecificationOption param = new TbSpecificationOption();
                //查询条件为规格id
                param.setSpecId(Long.parseLong(map.get("id").toString()));

                List<TbSpecificationOption> options = specificationOptionMapper.select(param);

                map.put("options", options);
            }
            //3.返回期望的数据
            return specList;
        }
        return null;
    }
}
