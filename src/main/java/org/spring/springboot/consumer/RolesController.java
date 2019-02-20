package org.spring.springboot.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import org.spring.springboot.domain.*;
import org.spring.springboot.dubbo.SysMenuService;
import org.spring.springboot.dubbo.SysRoleMenuService;
import org.spring.springboot.dubbo.SysRoleService;
import org.spring.springboot.response.*;
import org.spring.springboot.utils.JsonResponseGenerator;
import org.spring.springboot.utils.WebUtil;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
@Controller
@RequestMapping({"/role"})
public class RolesController {

    @Reference(version = "1.0.0",timeout = 30000)
    private SysRoleService sysRoleService;

    @Reference(version = "1.0.0",timeout = 30000)
    private SysRoleMenuService sysRoleMenuService;

    @Reference(version = "1.0.0",timeout = 30000)
    private SysMenuService sysMenuService;

    @RequestMapping(value={"index"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView listGet(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView mv = new ModelAndView("roles/index");
        return mv;
    }

    @ResponseBody
    @RequestMapping(value={"list"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object listPost(@RequestBody PagerQuery<SysRole> search, HttpServletRequest request, HttpServletResponse response) {

        SysRoleFindResponse sysRoleFindResponse = sysRoleService.findAllRoles(search);
        List<SysRole> list = new ArrayList<>();
        if(sysRoleFindResponse!=null){
            list = sysRoleFindResponse.getResult();
        }
        Map<String ,Object> map = new HashMap();
        map.put("recordsTotal",sysRoleFindResponse.getTotalCount() );
        map.put("recordsFiltered", sysRoleFindResponse.getTotalCount());
        map.put("data", list.toArray());
        return map; }

    @RequestMapping(value={"create"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView creatGet(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView mv = new ModelAndView("roles/create");
        return mv;
    }
    @ResponseBody
    @RequestMapping(value={"creat"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object creatPost(@RequestBody SysRole sysRole, HttpServletRequest request, HttpServletResponse response) {
        SysRoleCreateResponse sysRoleCreateResponse = new SysRoleCreateResponse();
        SysUser user = WebUtil.getCurrentUserByCode();
        sysRole.setCreatedBy(user.getId());
        sysRoleCreateResponse = sysRoleService.creatRole(sysRole);
        if(sysRoleCreateResponse.hasError()){
            return JsonResponseGenerator.fail(sysRoleCreateResponse.getErrors().get(0).getMessage());
        }else {
            if(1 == sysRoleCreateResponse.getResult()){
                return JsonResponseGenerator.success("添加成功!");
            }else{
                return JsonResponseGenerator.fail("添加失败!");
            }
        }
    }
    @RequestMapping(value={"delete"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView deleteGet(Integer id, HttpServletRequest request, HttpServletResponse response)
    {
        SysRole sysRole = sysRoleService.getById(id);
        ModelAndView mv = new ModelAndView("roles/delete");
        mv.addObject("sysRole", sysRole);
        return mv;
    }
    @ResponseBody
    @RequestMapping(value={"delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object delete(@RequestBody int id, HttpServletRequest request, HttpServletResponse response) {
        SysRoleDeleteResponse sysRoleDeleteResponse=new SysRoleDeleteResponse();
        sysRoleDeleteResponse = sysRoleService.deleteById(id);
        if(sysRoleDeleteResponse.hasError()){
            return JsonResponseGenerator.fail("删除失败!"+sysRoleDeleteResponse.getErrors().get(0).getMessage());
        }else {
            if(sysRoleDeleteResponse.getResult()==1){
                return JsonResponseGenerator.success("删除成功!");
            } else {
                return JsonResponseGenerator.fail("删除失败!");
            }
        }
    }

    @RequestMapping(value={"edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView editGet(Integer id, HttpServletRequest request, HttpServletResponse response)
    {
        SysRole sysRole = sysRoleService.getById(id);
        ModelAndView mv = new ModelAndView("roles/edit");
        mv.addObject("sysRole", sysRole);
        return mv;
    }
    @ResponseBody
    @RequestMapping(value={"edit"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object editPost(@RequestBody SysRole sysRole, HttpServletRequest request, HttpServletResponse response) {
        SysRoleUpdateResponse sysRoleUpdateResponse = new SysRoleUpdateResponse();
        sysRole.setLastUpdatedTime(new Date());
        SysUser user = WebUtil.getCurrentUserByCode();
        sysRole.setLastUpdatedBy(user.getId());
        sysRoleUpdateResponse = sysRoleService.updateRole(sysRole);
        if(sysRoleUpdateResponse.hasError()){
            return JsonResponseGenerator.fail("修改失败!"+sysRoleUpdateResponse.getErrors().get(0).getMessage());
        }else {
            if(sysRoleUpdateResponse.getResult()==1){
                return JsonResponseGenerator.success("修改成功!");
            } else {
                return JsonResponseGenerator.fail("修改失败!");
            }
        }
    }

    @RequestMapping(value={"rolemenulist"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView roleMenuGet(Integer id, HttpServletRequest request, HttpServletResponse response) {
        SysRole sysRole = sysRoleService.getById(id);
        ModelAndView mv = new ModelAndView("roles/roleMenu");
        mv.addObject("sysRole", sysRole);
        return mv;
    }
    @ResponseBody
    @RequestMapping(value={"rolemenulist"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object roleMenuPost(@RequestBody  PagerQuery<SysRoleMenu> search, HttpServletRequest request, HttpServletResponse response) {
        SysRoleMenuFindResponse sysRoleMenuFindResponse = new SysRoleMenuFindResponse();
        sysRoleMenuFindResponse = sysRoleMenuService.findRoleMenu(search);
        List<SysRoleMenuResponse> list = new ArrayList<>();
        if(sysRoleMenuFindResponse!=null){
            list = sysRoleMenuFindResponse.getResult();
        }
        Map<String ,Object> map = new HashMap();
        map.put("recordsTotal",sysRoleMenuFindResponse.getTotalCount() );
        map.put("recordsFiltered", sysRoleMenuFindResponse.getTotalCount());
        map.put("data", list.toArray());
        return map;
    }

    @ResponseBody
    @RequestMapping(value={"selectmenulist"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object roleMenuPost(@RequestBody  Integer id, HttpServletRequest request, HttpServletResponse response) {
        SysMenuFindResponse sysMenuFindResponse = new SysMenuFindResponse();
        sysMenuFindResponse = sysMenuService.getMenuList(id);
        List<SysMenu> list = new ArrayList<>();
        if(sysMenuFindResponse!=null){
            list = sysMenuFindResponse.getResult();
        }
        Map<String ,Object> map = new HashMap();
        map.put("recordsTotal",sysMenuFindResponse.getTotalCount() );
        map.put("recordsFiltered", sysMenuFindResponse.getTotalCount());
        map.put("data", list.toArray());
        map.put("success", "success");
        return map;
    }

    @ResponseBody
    @RequestMapping(value={"creatRoleMenu"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object creatRomeMenuPost(@RequestBody SysRoleMenu sysRoleMenu, HttpServletRequest request, HttpServletResponse response) {
        SysRoleMenuCreateResponse CreateResponse  = new SysRoleMenuCreateResponse();
        SysUser user = WebUtil.getCurrentUserByCode();
        sysRoleMenu.setCreatedBy(user.getId());
        sysRoleMenu.setCreatedTime(new Date());
        CreateResponse= sysRoleMenuService.createRoleMenu(sysRoleMenu);
        if(CreateResponse.hasError()){
            return JsonResponseGenerator.fail(CreateResponse.getErrors().get(0).getMessage());
        }else {
            if(1 == CreateResponse.getResult()){
                return JsonResponseGenerator.success("添加成功!");
            }else{
                return JsonResponseGenerator.fail("添加失败!");
            }
        }
    }
}
