package org.spring.springboot.consumer;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import org.spring.springboot.domain.PagerQuery;
import org.spring.springboot.domain.SysRole;
import org.spring.springboot.domain.SysUser;
import org.spring.springboot.dubbo.SysRoleService;
import org.spring.springboot.dubbo.SysUserService;
import org.spring.springboot.response.SysRoleFindResponse;
import org.spring.springboot.response.SysUserFindResponse;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Controller
@RequestMapping({"/role"})
public class RolesController {

    @Reference(version = "1.0.0",timeout = 30000)
    private SysRoleService sysRoleService;

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
        Map map = new HashMap();
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
        SysUser user = WebUtil.getCurrentUserByCode();
        sysRole.setCreatedBy(user.getId());
        if(sysRoleService.creatRole(sysRole)==1){
         return JsonResponseGenerator.success("添加成功!");
     }else{
         return JsonResponseGenerator.fail("名称已经存在!");
     }

    }

}
