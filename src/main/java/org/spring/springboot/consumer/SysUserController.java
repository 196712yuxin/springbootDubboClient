package org.spring.springboot.consumer;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import org.spring.springboot.domain.PagerQuery;
import org.spring.springboot.domain.SysUser;
import org.spring.springboot.dubbo.SysUserService;
import org.spring.springboot.response.SysUserFindResponse;
import org.spring.springboot.utils.JsonResponseGenerator;
import org.spring.springboot.utils.MD5Util;
import org.spring.springboot.utils.WebUtil;
import org.spring.springboot.utils.WorkStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Controller
@RequestMapping({"/sysuser"})
public class SysUserController
{

    @Reference(version = "1.0.0",timeout = 30000)
    private SysUserService sysUserService;

    @RequestMapping(value={"list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView listGet(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView mv = new ModelAndView("sysUser/list");
        return mv;
    }
    @ResponseBody
    @RequestMapping(value={"list"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object listPost(@RequestBody PagerQuery<SysUser> search, HttpServletRequest request, HttpServletResponse response) {
        PageHelper.startPage(search.getPage().intValue(), search.getLength().intValue());
        if (StringUtils.isNotEmpty(search.getOrderColumn())) {
            PageHelper.orderBy(search.getOrderColumn() + " " + search.getOrderDir());
        }
        SysUserFindResponse sysUserFindResponse = this.sysUserService.selectEverywhere(search);
        List<SysUser> list = new ArrayList<>();
        if(sysUserFindResponse!=null){
            list = sysUserFindResponse.getResult();
        }
        Map map = new HashMap();
        map.put("recordsTotal",sysUserFindResponse.getTotalCount() );
        map.put("recordsFiltered", sysUserFindResponse.getTotalCount());
        map.put("data", list.toArray());
        return map; }

    @RequestMapping(value={"edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView editGet(Integer id, HttpServletRequest request, HttpServletResponse response) {
        SysUser sysUser = this.sysUserService.selectByPrimaryKey(id);
        ModelAndView mv = new ModelAndView("sysUser/edit");
        mv.addObject("sysUser", sysUser);
        return mv;
    }
    @ResponseBody
    @RequestMapping(value={"edit"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object editPost(@RequestBody SysUser record, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        SysUser user = WebUtil.getCurrentUserByCode();

        if ((record.getId() == null) || (record.getId().intValue() == 0)) {
            boolean pd=false;
            if(record.getPassword()!=null) {
                pd = WorkStringUtils.regExpPd(record.getPassword());
            }else {
                pd=false;
            }
            if (!pd) {
                return JsonResponseGenerator.fail("添加失败，密码必须包含数字和字母组合，且长度在8到15位之内!");
            }
            record.setPassword(MD5Util.md5(record.getPassword()));
            record.setIsActive("Y");
            record.setLoginErrCount(Integer.valueOf(0));
            record.setCreatedBy(user.getId());
            record.setCreatedTime(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar rightNow = Calendar.getInstance();
            String str = sdf.format(rightNow.getTime());
            Date dt = sdf.parse(str);
            rightNow.setTime(dt);
            rightNow.add(1, 1);
            Date dt1 = rightNow.getTime();
            record.setPwdexpiredate(dt1);
            int count = this.sysUserService.getByNameTotal(record.getUsername());
            if (count > 0) {
                return JsonResponseGenerator.fail("用户名已经存在!");
            }
            this.sysUserService.insert(record);
        } else {
            record.setPassword(null);
            String name = this.sysUserService.getNameById(record.getId());
            if (!record.getUsername().equals(name)) {
                int count = this.sysUserService.getByNameTotals(record.getUsername(), record.getId());
                if (count > 0) {
                    return JsonResponseGenerator.fail("用户名已经存在!");
                }
            }
            this.sysUserService.updateById(record);
        }
        return JsonResponseGenerator.success("添加成功!");
    }

    @RequestMapping(value={"delete"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView deleteGet(Integer id, HttpServletRequest request, HttpServletResponse response) {
        SysUser sysUser = this.sysUserService.selectByPrimaryKey(id);
        ModelAndView mv = new ModelAndView("sysUser/delete");
        mv.addObject("sysUser", sysUser);
        return mv;
    }
    @ResponseBody
    @RequestMapping(value={"delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object deletePost(@RequestBody SysUser record, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        SysUser user = WebUtil.getCurrentUserByCode();
            record.setIs_delete(true);
            record.setIsActive("N");
            record.setLastUpdatedBy(user.getId());
            record.setLastUpdatedTime(new Date());
            this.sysUserService.updateById(record);
        return JsonResponseGenerator.success("删除成功!");
    }

    @RequestMapping(value={"create"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView creatGet(Integer id, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView("sysUser/creat");
        return mv;
    }

    @ResponseBody
    @RequestMapping(value={"create"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object createPost(@RequestBody SysUser record, HttpServletRequest request, HttpServletResponse response) throws ParseException {
       /*校验登录名*/
        int req=sysUserService.getByNameTotal(record.getUsername());
        if(req==1){
            return JsonResponseGenerator.fail("登录名称已存在，添加失败!");
        }
        SysUser user = WebUtil.getCurrentUserByCode();
        record.setIs_delete(false);
        record.setIsActive("Y");
        record.setIsLocked("N");
        record.setCreatedBy(user.getId());
        record.setCreatedTime(new Date());
        int result=this.sysUserService.insert(record);
        if(result==1){
            return JsonResponseGenerator.success("添加成功!");
        }else {
            return JsonResponseGenerator.fail("添加失败!");
        }
    }
}
