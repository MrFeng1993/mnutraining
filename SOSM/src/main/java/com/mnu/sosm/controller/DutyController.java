package com.mnu.sosm.controller;


import com.mnu.sosm.Criteria.DutyCriteria;
import com.mnu.sosm.Criteria.TaskCriteria;
import com.mnu.sosm.dao.IDutyInfoDao;
import com.mnu.sosm.dao.ITaskDao;
import com.mnu.sosm.dto.DutySettingDto;
import com.mnu.sosm.entity.DutyInfo;
import com.mnu.sosm.entity.MyUser;
import com.mnu.sosm.entity.Task;
import com.mnu.sosm.service.DutyService;
import com.mnu.sosm.utils.JsonModel;
import com.mnu.sosm.utils.PageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/duty")
@Slf4j
public class DutyController {




    @Resource
    private IDutyInfoDao dutyInfoDao;

    @Resource
    private DutyService dutyService;

    /**
     * 查询（分页）
     * @param
     * @return
     */
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public JsonModel search(@RequestBody DutyCriteria criteria){
        try {
            PageModel page = criteria.findByCriteria(dutyInfoDao);
            return new JsonModel(true, "操作成功", page);

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonModel(false,e.getMessage());
        }

    }

    /**
     * 单个查询
     * @param
     * @return
     */
    @RequestMapping(value = "/findOne/{id}",method = RequestMethod.GET)
    public JsonModel findOne(@PathVariable("id") Long id){
        try {
            Assert.notNull(id,"缺少必要参数");
            boolean e = dutyInfoDao.existsById(id);
            if (!e){

                return new JsonModel(false,"数据不存在");
            }
            DutyInfo dutyInfo = dutyInfoDao.findOne(id);
            return new JsonModel(true,"操作成功",dutyInfo);

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonModel(false,e.getMessage());
        }

    }


    /**
     * 单个查询
     * @param
     * @return
     */
    @RequestMapping(value = "/findDutyUser/{id}",method = RequestMethod.GET)
    public JsonModel findDutyUser(@PathVariable("id") Long id){
        try {
            Assert.notNull(id,"缺少必要参数");

            List<MyUser> users = dutyService.findDutyUser(id);
            if (users == null){
                users = new ArrayList<>();
            }
            return new JsonModel(true,"操作成功",users);

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonModel(false,e.getMessage());
        }

    }

    /**
     * 排班设置
     * @param
     * @return
     */
    @RequestMapping(value = "/scheduling",method = RequestMethod.POST)
    public JsonModel scheduling(@RequestBody DutySettingDto param){
        try {
            if (param == null) {
                return new JsonModel(true,"参数错误",param);
            }else {
                Assert.notNull(param.getDutyId(),"缺少排班ID");
                Assert.isTrue(param.getUserIds() != null && param.getUserIds().size() > 0,"缺少用户ID");
                dutyService.scheduling(param);
                return new JsonModel(true,"操作成功");
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonModel(false,e.getMessage());
        }

    }

}
