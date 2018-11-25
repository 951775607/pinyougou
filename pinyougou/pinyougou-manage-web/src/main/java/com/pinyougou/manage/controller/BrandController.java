package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

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
     * 根据条件分页查询
     * @param brand 查询条件
     * @param page 页号
     * @param rows 页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, @RequestParam(value =
            "page", defaultValue = "1")Integer page,
                             @RequestParam(value = "rows", defaultValue = "10")Integer
                                     rows) {
        return brandService.search(brand,page,rows);
    }


    /**
     * 删除用户，可以实现批量删除
     * param:数组
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            brandService.deleteByIds(ids); return Result.ok("删除成功");
        } catch (Exception e) { e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 根据id查询一个用户
     */
    @GetMapping("/findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand); return Result.ok("修改成功");
        } catch (Exception e) { e.printStackTrace();
        }
        return Result.fail("修改失败");
    }


    /**
     * 新增用户
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand) {
        try {
                brandService.add(tbBrand);
                return Result.ok("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("保存失败");
    }

    /**
     * 根据条件分页查询
     */
//    @PostMapping("/search")
//    public PageResult search(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10")Integer pageSize,@RequestBody TbBrand tbBrand) {
//
//         PageResult pageResult= brandService.search(tbBrand,page,pageSize);
//         return pageResult;
//    }

    /**
     * 根据页号和页大小查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows){
//        return brandService.testPage(page, rows);
        return (List<TbBrand>) brandService.findPage(page, rows).getRows();
    }


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

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue =
            "1")Integer page,
                               @RequestParam(value = "rows", defaultValue =
                                       "10")Integer rows) {
        return brandService.findPage(page, rows);
    }
}
