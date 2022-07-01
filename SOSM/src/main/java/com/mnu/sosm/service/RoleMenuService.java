package com.mnu.sosm.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mnu.sosm.dao.IMenuDao;
import com.mnu.sosm.dao.IMyRoleDao;
import com.mnu.sosm.dao.IRoleMenuDao;
import com.mnu.sosm.dto.MenuSettingDto;
import com.mnu.sosm.entity.Menu;
import com.mnu.sosm.entity.RoleMenu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: SOSM
 * @BelongsPackage: com.mnu.sosm.service
 * @Author: fenggn
 * @CreateTime: 2022-06-26  08:02
 * @Description: TODO
 * @Version: 1.0
 */

@Service
@Slf4j
public class RoleMenuService {

    @Resource
    private IMyRoleDao roleDao;

    @Resource
    private IMenuDao menuDao;

    @Resource
    private IRoleMenuDao iRoleMenuDao;


    /**
     * 根据角色ID获取角色对应的菜单权限
     * @param roleId
     * @return
     */
    public Object getMenuByRoleId(Long roleId){
        List<RoleMenu> roleMenus = iRoleMenuDao.findByRoleId(roleId);
        if (roleMenus != null && roleMenus.size() > 0){
            List<Long> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
            List<Menu> menus = menuDao.findByIdIn(menuIds);
            if (menus != null && menus.size() > 0){
//                return menuDataHandler(menus);
                return menus;
            }
        }
        return new JSONArray();
    }



    /**
     * 按照前端要求组装菜单数组
     * @return
     */
    public JSONArray menuDataHandler(List<Menu> menus){

        List<Menu> topMenus = menus.stream().filter(m -> {
            return m.getPid() == 0;
        }).collect(Collectors.toList());

        if (topMenus != null && topMenus.size() > 0){
            for (Menu topMenu : topMenus) {
                if (topMenu.getList() == null){
                    topMenu.setList(new ArrayList<>());
                }
                for (Menu menu : menus) {
                    if (topMenu.getId() == menu.getPid()){
                        menu.setList(new ArrayList<>());
                        topMenu.getList().add(menu);
                    }
                }
            }
            return JSONArray.parseArray(JSON.toJSONString(topMenus));
        }
        return new JSONArray();

    }

    /**
     * 设置角色菜单
     * @param menuSetting
     */
    @Transactional
    public void menuSetting(MenuSettingDto menuSetting) {
        Long roleId = menuSetting.getRoleId();
        iRoleMenuDao.deleteByRoleId(roleId);

        //创建
        List<RoleMenu> roleMenus = menuSetting.getMenuIds().stream().map(
                menuId->{
                    RoleMenu roleMenu = new RoleMenu(roleId,menuId);
                    return roleMenu;
                }
        ).collect(Collectors.toList());
        iRoleMenuDao.saveAll(roleMenus);
    }
}
