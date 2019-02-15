package org.spring.springboot.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.google.gson.Gson;
import org.spring.springboot.annotaction.AuthenticationSupport;
import org.spring.springboot.annotaction.NoAuthoring;
import org.spring.springboot.annotaction.PrivilegeSupport;
import org.spring.springboot.base.base.BaseController;
import org.spring.springboot.domain.ErrorStatus;
import org.spring.springboot.domain.SysMenu;
import org.spring.springboot.domain.SysUser;
import org.spring.springboot.domain.UserInfo;
import org.spring.springboot.dubbo.SysMenuService;
import org.spring.springboot.dubbo.SysUserService;
import org.spring.springboot.dubbo.doman.SysUserCook;
import org.spring.springboot.utils.EncryptUtil;
import org.spring.springboot.utils.MD5Util;
import org.spring.springboot.utils.WebUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.spi.http.HttpContext;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Controller
@RequestMapping({"/"})
public class HomeController extends BaseController {

    private final EncryptUtil utils= EncryptUtil.getInstance();

    @Reference(version = "1.0.0",timeout = 30000)
    private SysUserService sysUserService;

    @Reference(version = "1.0.0",timeout = 30000)
    private SysMenuService sysMenuService;

    @AuthenticationSupport(support=false)
    @PrivilegeSupport(support=false)
    @RequestMapping(value={"/login"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String loginGet(HttpServletRequest request)
    {
        return "login"; }
    @RequestMapping(value={"/login"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @AuthenticationSupport(support=false)
    @PrivilegeSupport(support=false)
    public ModelAndView loginPost(Model model, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String username = request.getParameter("signin_username");
        String password = request.getParameter("signin_password");
        sysUserService.selectByPrimaryKey(1);

        ErrorStatus status = new ErrorStatus();
        String ss=WebUtil.getRemortIP(request);
        Map mapLoginUser = this.sysUserService.login(username, MD5Util.md5(password), ss, status);

        ModelAndView mv = new ModelAndView();
        if (status.getErrorCode() == -4) {
            mv = new ModelAndView("redirect:/changepwd");
            mv.addObject("username", username);
            return modelAndView(mv, request, response);
        }
        if (mapLoginUser != null) {
            SysUser loginUser = (SysUser)mapLoginUser.get("domain");
            UserInfo userInfo = new UserInfo();
            userInfo.setUser(loginUser);
            findMenus(userInfo);
            SysUserCook sysUserCook = new SysUserCook();
            BeanUtils.copyProperties(loginUser, sysUserCook);
            String userCook= JSON.toJSONString(sysUserCook);
            String userCook1=sysUserCook.getId()+"";
            String coo= utils.Base64Encode(userCook);
            URLEncoder.encode(userCook,"UTF-8");
            Cookie cookie = new Cookie("user", coo);
            cookie.setMaxAge(15 * 600);
            cookie.setPath("/");
            response.addCookie(cookie);

            request.getSession().setAttribute("currentUser", userInfo);
            mv = new ModelAndView("redirect:/dashboard");
            mv.addObject("username", username);
            return modelAndView(mv, request, response);
        }
        model.addAttribute("errmsg", status.getErrorMessage());
        mv = new ModelAndView("redirect:/login");
        return modelAndView(mv, request, response); }

    @PrivilegeSupport(support=false)
    @RequestMapping({"/dashboard"})
    public String dashboard(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        return "main";
    }
    @RequestMapping(value={"/logout"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @PrivilegeSupport(support=false)
    public String logoutGet(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) { session.removeAttribute("currentUser");
        return modelAndView("redirect:/login", request, response);
    }

    @RequestMapping(value={"/changepwd"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @NoAuthoring
    public String changepwdGet(Model model, HttpServletRequest request, HttpServletResponse response)
    {
        SysUser sysUser = WebUtil.getCurrentUser();
        if (sysUser != null) {
            model.addAttribute("Name", sysUser.getUsername());
        }
        return modelAndView("changepwd", request, response); }
    @ResponseBody
    @RequestMapping(value={"/changepwd"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @NoAuthoring
    public ModelAndView changepwdPost(Model model, HttpServletRequest request, HttpServletResponse response) { ModelAndView mv = new ModelAndView();
        String username = request.getParameter("username");
        String password = request.getParameter("oldpwd");
        String newpwd = request.getParameter("newpwd");

        return super.modelAndView(mv, request, response); }
    @RequestMapping(value={"/accessdenied"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @AuthenticationSupport(support=false)
    @PrivilegeSupport(support=false)
    public String accessdenied(Model model, HttpServletRequest request, HttpServletResponse response) { return modelAndView("accessdenied", request, response); }

    private void findMenus(UserInfo userInfo)
    {   UserInfo userInfo1 = new UserInfo();
        UserInfo.Menu rootMenu =userInfo1.new  Menu();
        userInfo.setRootMenu(rootMenu);

        Map data = this.sysMenuService.getAllMenu(userInfo.getUser().getId().intValue());

        SysMenu rootSysMenu = (SysMenu)data.get("root");
        List menus = rootSysMenu.getSubMenus();
        for (int i = 0; i < menus.size(); i++) {
            SysMenu sysMenu = (SysMenu)menus.get(i);
            wapperSubMenu(rootMenu, sysMenu);
        }

        List menuPaths = new ArrayList();
        userInfo.setMenuPaths(menuPaths);

        List sysMenuPaths = (List)data.get("path");
        for (int i = 0; i < sysMenuPaths.size(); i++) {
            List sysMenuPath = (List)sysMenuPaths.get(i);
            wapperMenuPath(menuPaths, sysMenuPath);
        }
    }

    private void wapperMenuPath(List<List<UserInfo.Menu>> menuPaths, List<SysMenu> sysMenuPath) {
        List newMenuPath = new ArrayList();
        for (int i = 0; i < sysMenuPath.size(); i++) {
            SysMenu oldMenu = (SysMenu)sysMenuPath.get(i);
            newMenuPath.add(wapperMenu(oldMenu));
        }

        if (!newMenuPath.isEmpty())
            menuPaths.add(newMenuPath);
    }

    private void wapperSubMenu(UserInfo.Menu rootMenu, SysMenu menu) {
        if (menu == null) {
            return;
        }
        UserInfo.Menu wapper = wapperMenu(menu);
        rootMenu.addSubMenu(wapper);

        List subMenu = menu.getSubMenus();
        for (int i = 0; i < subMenu.size(); i++)
            wapperSubMenu(wapper, (SysMenu)subMenu.get(i));
    }

    private UserInfo.Menu wapperMenu(SysMenu menu)
    {
        UserInfo userInfo1 = new UserInfo();
        UserInfo.Menu wapper = userInfo1.new Menu();
        wapper.setId(menu.getId());
        wapper.setMenuName(menu.getMenuName());
        wapper.setMenuIcon(menu.getMenuIcon());
        wapper.setMenuDesc(menu.getMenuDesc());
        wapper.setMenuHref(menu.getMenuHref());
        wapper.setMenuType(menu.getMenuType());
        wapper.setSort(menu.getSort());
        wapper.setIsShow(menu.getIsShow());
        return wapper;
    }


}
