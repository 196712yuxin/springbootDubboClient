package org.spring.springboot.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import org.spring.springboot.domain.City;
import org.spring.springboot.domain.PagerQuery;
import org.spring.springboot.domain.SysMenu;
import org.spring.springboot.domain.SysRole;
import org.spring.springboot.dubbo.CityDubboService;
import org.spring.springboot.response.SysRoleFindResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

/**
 *
 *
 * Created by bysocket on 28/02/2017.
 */
@Component
@Controller
@RequestMapping({"/sysmenu"})
public class SysMenuService {

    @RequestMapping(value={"index"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView listGet(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView mv = new ModelAndView("sysmenu/index");
        return mv;
    }
    @ResponseBody
    @RequestMapping(value={"list"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public Object listPost(@RequestBody PagerQuery<SysMenu> search, HttpServletRequest request, HttpServletResponse response) {


        List<SysRole> list = new ArrayList<>();

        Map map = new HashMap();

        map.put("data", list.toArray());
        return map; }

}
