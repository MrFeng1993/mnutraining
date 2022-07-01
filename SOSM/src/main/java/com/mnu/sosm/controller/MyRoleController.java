package com.mnu.sosm.controller;


import com.alibaba.fastjson.JSONArray;
import com.mnu.sosm.dao.IMyRoleDao;
import com.mnu.sosm.dto.MenuSettingDto;
import com.mnu.sosm.entity.Menu;
import com.mnu.sosm.entity.MyRole;
import com.mnu.sosm.service.MyUserService;
import com.mnu.sosm.service.RoleMenuService;
import com.mnu.sosm.utils.JsonModel;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ManyToAny;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/role")
@Slf4j
public class MyRoleController {

    @Resource
    private IMyRoleDao iMyRoleDao;

    @Resource
    private RoleMenuService roleMenuService;

    @Resource
    private MyUserService userService;


    /**
     * 新增/编辑
     * @param role
     * @return
     */
    @Transactional
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public JsonModel update(@RequestBody MyRole role){
        if (role .getId() == null) {//新增
            boolean ec = iMyRoleDao.existsByRoleCodeAndStatus(role.getRoleCode(),(byte)1);
            if (ec){
                return new JsonModel(false,"roleCode重复",role);
            }
            boolean en = iMyRoleDao.existsByRoleNameAndStatus(role.getRoleName(),(byte)1);
            if (en){
                return new JsonModel(false,"roleName重复",role);
            }
            role.setCreateTime(new Date());
            role.setStatus((byte) 1);
            role = iMyRoleDao.save(role);
        }else {//编辑

            boolean ec = iMyRoleDao.existsByRoleCodeAndStatusAndIdIsNot(role.getRoleCode(),(byte)1,role.getId());
            if (ec){
                return new JsonModel(false,"roleCode重复",role);
            }
            boolean en = iMyRoleDao.existsByRoleNameAndStatusAndIdIsNot(role.getRoleCode(),(byte)1,role.getId());
            if (en){
                return new JsonModel(false,"roleName重复",role);
            }
            role = iMyRoleDao.update(role.getId(),role);
        }
        return new JsonModel(true,"操作成功",role);
    }

    /**
     * 单个删除
     * @param id
     * @return
     */
    @Transactional
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.DELETE)
    public JsonModel delete(@PathVariable("id")Long id){
        try {
            Assert.notNull(id,"缺少必要参数");
            boolean can = userService.canDeleteRole(id);
            if (!can) {
                boolean exist = iMyRoleDao.existsById(id);
                if (!exist) {
                    return new JsonModel(false,"角色不存在");
                }
                MyRole role = new MyRole();
                role.setId(id);
                role.setStatus((byte) -1);
                iMyRoleDao.update(id,role);
                return new JsonModel(true,"操作成功");

            }else {
                return new JsonModel(false,"存在绑定用户不能删除");
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonModel(false,e.getMessage());
        }

    }


    /**
     * 查询（不分页）
     * @param
     * @return
     */
    @RequestMapping(value = "/findAll",method = RequestMethod.GET)
    public JsonModel findAll(){
        try {
            List<MyRole> roles = iMyRoleDao.findAllAvailableRoles();
            if (roles == null || roles.size() ==0) {
                return new JsonModel(false,"暂无数据",roles);
            }
            return new JsonModel(true,"操作成功",roles);

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonModel(false,e.getMessage());
        }

    }

    /**
     * 查询（不分页）
     * @param
     * @return
     */
    @RequestMapping(value = "/findOne/{id}",method = RequestMethod.GET)
    public JsonModel findOne(@PathVariable("id") Long id){
        try {
            Assert.notNull(id,"缺少必要参数");
            MyRole role = iMyRoleDao.findMyRoleByIdAndStatus(id,(byte)1);
            if (role == null) {
                return new JsonModel(false,"数据不存在",role);
            }
            return new JsonModel(true,"操作成功",role);

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonModel(false,e.getMessage());
        }

    }

    @RequestMapping(value = "/getMenu/{id}", method = RequestMethod.GET)
    public JsonModel getMenu(@PathVariable("id") Long id){
        try {
            Assert.notNull(id,"缺少必要参数");
            Object ret = roleMenuService.getMenuByRoleId(id);

            return new JsonModel(true,"操作成功",ret);

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonModel(false,e.getMessage());
        }
    }


    @Transactional
    @RequestMapping(value = "setMenu",method = RequestMethod.POST)
    public JsonModel setMenu(@RequestBody MenuSettingDto menuSetting){
        try {
            Assert.isTrue(menuSetting != null
                    && menuSetting.getRoleId() !=null
                    && menuSetting.getMenuIds() !=null
                    && menuSetting.getMenuIds().size() > 0,"参数错误");
            roleMenuService.menuSetting(menuSetting);
            return new JsonModel(true,"操作成功");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonModel(false,e.getMessage());
        }


    }
}
