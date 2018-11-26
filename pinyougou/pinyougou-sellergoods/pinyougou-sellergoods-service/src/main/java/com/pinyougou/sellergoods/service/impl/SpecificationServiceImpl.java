package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = SpecificationService.class)
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSpecification specification) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(specification.getSpecName())){
            criteria.andLike("specName", "%" + specification.getSpecName() + "%");
        }

        List<TbSpecification> list = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    public void add(Specification specification) {
        //保存规格；通用mapper可以在执行新增之后回填主键
        specificationMapper.insertSelective(specification.getSpecification());

        //保存规格选项列表
        if (specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size() > 0) {
            for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
                //设置规格id
                specificationOption.setSpecId(specification.getSpecification().getId());
                //保存规格选项
                specificationOptionMapper.insertSelective(specificationOption);
            }
        }

    }


    /**
    * 通过规格 ID，到后端查询规格和规格选项列表
    * */
    @Override
    public Specification findOne(Long id) {
        //返回一个规格
        Specification specification = new Specification();
        //查询并设置规格
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        specification.setSpecification(tbSpecification);

        //查询并设置规格选项列表
        //创建规格选项列表对象
        TbSpecificationOption param = new TbSpecificationOption();
        param.setSpecId(id);
        List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.select(param);
        //把规格列表封装到规格对象
        specification.setSpecificationOptionList(specificationOptionList);
        //返回规格
        return specification;
    }

    /**
    * 修改规格
    * */
    @Override
    public void update(Specification specification) {
        //1、更新规格
        update(specification.getSpecification());

        //2、删除规格对应的所有规格选项
        TbSpecificationOption param = new TbSpecificationOption();
        param.setSpecId(specification.getSpecification().getId());
        specificationOptionMapper.delete(param);

        //3、保存规格选项列表
        if (specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size() > 0) {
            for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
                //设置规格id
                specificationOption.setSpecId(specification.getSpecification().getId());
                //保存规格选项
                specificationOptionMapper.insertSelective(specificationOption);
            }
        }
    }

    /**
     * 删除规格。删除规格的同时需要把规格对应的选项删除
    * */
    @Override
    public void deleteSpecificationByIds(Long[] ids) {
        //批量删除规格
        deleteByIds(ids);

        //批量删除规格选项
        Example example = new Example(TbSpecificationOption.class);
        example.createCriteria().andIn("specId", Arrays.asList(ids));
        specificationOptionMapper.deleteByExample(example);
    }
}
