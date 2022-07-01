package com.mnu.sosm;

import com.mnu.sosm.dao.*;
import com.mnu.sosm.entity.*;
import com.mnu.sosm.service.DutyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Configuration
@Slf4j
public class SosmStartInitHandler implements InitializingBean {

    @Resource
    private IMyRoleDao iMyRoleDao;

    @Resource
    private IMyUserDao iMyUserDao;

    @Resource
    private IMenuDao menuDao;

    @Resource
    private IRoleMenuDao roleMenuDao;


    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private DutyService dutyService;

    @Override
    public void afterPropertiesSet() throws Exception {


        try {

            log.info("开始系统初始化......");
            initSysDefaultRoleAndUser();

            initSysDefaultMenu();

            dutyService.initDutyInfo();

            log.info("系统初始化完成");

        } catch (Exception e) {
            log.error("初始化错误",e);
        }
    }

    /**
     * 初始化系统默认角色和超级管理员
     */
    @Transactional
    public void initSysDefaultRoleAndUser(){
        MyRole super_admin = iMyRoleDao.findOneByRoleCode("super_admin");
        if (super_admin == null){
            log.info("初始化超级管理员角色");
            super_admin = new MyRole();
            super_admin.setCreateTime(new Date());
            super_admin.setRoleCode("super_admin");
            super_admin.setStatus((byte)1);
            super_admin.setRoleName("jaychou");
            super_admin.setRoledesc("周董要发专辑了，准备过年了");
            super_admin = iMyRoleDao.save(super_admin);
        }

        MyUser sys_user = iMyUserDao.findByAccount("admin");
        if (sys_user == null) {
            log.info("初始化超级管理员用户");
            sys_user = new MyUser();
            sys_user.setDepartment("杰威尔");
            sys_user.setRoleId(super_admin.getId());
            sys_user.setPassword(passwordEncoder.encode("123456"));
            sys_user.setStatus((byte)1);
            sys_user.setCreateTime(new Date());
            sys_user.setUserName("jaychou");
            sys_user.setAccount("admin");
            iMyUserDao.save(sys_user);
        }
    }

    /**
     * 初始化系统默认角色和超级管理员
     */
    @Transactional
    public void initSysDefaultMenu(){
        log.info("初始化系统菜单....");
        List<Menu> menus = menuDao.findAll();
        if (menus == null || menus.size() == 0){
            boolean has_super_admin = iMyRoleDao.existsByRoleCode("super_admin");
            if (!has_super_admin){
                initSysDefaultRoleAndUser();
            }
            MyRole super_admin = iMyRoleDao.findOneByRoleCode("super_admin");
            Long super_role_id = super_admin.getId();
            menus = new ArrayList<>();
            Date now = new Date();
            Menu menu1 = new Menu(null,"权限管理","",null,0l,now,null);
            Menu menu2 = new Menu(null,"角色管理","/role",null,1l,now,null);
            Menu menu3 = new Menu(null,"用户管理","/user",null,1l,now,null);
            Menu menu4 = new Menu(null,"排班管理","/duty",null,0l,now,null);
            Menu menu5 = new Menu(null,"任务管理","/task",null,0l,now,null);
            menus.add(menu1);
            menus.add(menu2);
            menus.add(menu3);
            menus.add(menu4);
            menus.add(menu5);

            //保存默认菜单信息
            menus = menuDao.saveAllAndFlush(menus);

            List<RoleMenu> role_menus = new ArrayList<>();
            for (Menu menu : menus) {
                RoleMenu roleMenu = new RoleMenu(super_role_id,menu.getId());
                role_menus.add(roleMenu);
            }
            //保存菜单和超级管理员的关系
            roleMenuDao.saveAllAndFlush(role_menus);

        }
        log.info("初始化系统菜单初始化完成");
    }

}
