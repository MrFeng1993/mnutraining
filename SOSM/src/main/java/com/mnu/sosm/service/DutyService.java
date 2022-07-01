package com.mnu.sosm.service;

import com.mnu.sosm.dao.IDutyInfoDao;
import com.mnu.sosm.dao.IDutyUserDao;
import com.mnu.sosm.dao.IMyUserDao;
import com.mnu.sosm.dto.DutySettingDto;
import com.mnu.sosm.entity.DutyInfo;
import com.mnu.sosm.entity.DutyUser;
import com.mnu.sosm.entity.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: SOSM
 * @BelongsPackage: com.mnu.sosm.service
 * @Author: fenggn
 * @CreateTime: 2022-06-28  16:25
 * @Description: TODO
 * @Version: 1.0
 */

@Service
@Slf4j
public class DutyService {


//    private static SimpleDateFormat = SimpleDateFormat();

    private static String DATE_FORMAT = "yyyy-MM-dd";

    private static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    private static SimpleDateFormat FORMATOR_DATE = new SimpleDateFormat(DATE_FORMAT);

    private static SimpleDateFormat FORMATOR_DATE_TIME = new SimpleDateFormat(DATE_TIME_FORMAT);


    @Resource
    private IDutyInfoDao dutyInfoDao;

    @Resource
    private IDutyUserDao dutyUserDao;

    @Resource
    private IMyUserDao userDao;

    @Scheduled(cron = "0 * * * * ?")
    public void dutyTimer(){

        log.info("开始检查并自动补全排班信息");
        initDutyInfo();
        log.info("排班信息定时任务完成");

    }

    /**
     * 根据开始结束时间输出排班序列
     * @param start
     * @param end
     * @return
     */
    public static List<DutyInfo> calDutyPlan(Date start, Date end){

        Calendar sc = Calendar.getInstance();
        sc.setTime(start);
        Calendar ec = Calendar.getInstance();
        ec.setTime(end);

        //检查时间顺序是否颠倒，减少后续条件判断
        try {
            Assert.isTrue( ec.compareTo(sc) >= 0,"时间顺序错误");
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        int dayNum = 0;
        if (sc.get(Calendar.YEAR) == ec.get(Calendar.YEAR)){//不跨年
            dayNum = ec.get(Calendar.DAY_OF_YEAR)-sc.get(Calendar.DAY_OF_YEAR);
        }else {//跨年
            dayNum = sc.getMaximum(Calendar.DAY_OF_YEAR) - sc.get(Calendar.DAY_OF_YEAR)
                    + ec.get(Calendar.DAY_OF_YEAR);
        }

        List<DutyInfo> ret = new ArrayList<>();
        for (int i = 0; i <= dayNum; i++) {
            Calendar scc = Calendar.getInstance();
            scc.setTime(start);
            scc.add(Calendar.DAY_OF_YEAR,i);
            Date currentDate = scc.getTime();

            //小时以后的全部置零
            scc.set(Calendar.MINUTE,0);
            scc.set(Calendar.SECOND,0);
            scc.set(Calendar.MILLISECOND,0);

            //白班信息
            DutyInfo d1 = new DutyInfo();
            d1.setDutyDate(FORMATOR_DATE.format(currentDate));
            scc.set(Calendar.HOUR_OF_DAY,8);
            d1.setStartTime(scc.getTime());
            scc.add(Calendar.HOUR_OF_DAY,12);
            d1.setEndTime(scc.getTime());
            d1.setDailyOrder((byte)1);
            d1.setCreateTime(new Date());
            ret.add(d1);


            //夜班信息
            DutyInfo d2 = new DutyInfo();
            d2.setDutyDate(FORMATOR_DATE.format(currentDate));
            d2.setStartTime(scc.getTime());
            scc.add(Calendar.HOUR_OF_DAY,12);
            d2.setEndTime(scc.getTime());
            d2.setDailyOrder((byte)2);
            d2.setCreateTime(new Date());
            ret.add(d2);

            log.info("add duty of " + FORMATOR_DATE.format(currentDate));

        }
        return ret;
    }


    /**
     * 自动补全往后7天之内的排班信息
     */
    public void initDutyInfo(){
        log.info("initDutyInfo starting............");

        DutyInfo latestDutyInfo = dutyInfoDao.findLatestDuty();
        Date start = new Date();
        if (latestDutyInfo != null){
            start = latestDutyInfo.getEndTime();
        }

        Calendar sc = Calendar.getInstance();
        sc.setTime(start);
        Calendar ec = Calendar.getInstance();
        ec.add(Calendar.DAY_OF_YEAR,7);

        //只要ec>sc哪怕一秒都要补充第七天的排班
        if (ec.compareTo(sc) >= 0 ){
            List<DutyInfo> dutyInfos = calDutyPlan(start,ec.getTime());
            if (dutyInfos != null && dutyInfos.size() > 0){
                dutyInfoDao.saveAll(dutyInfos);
            }
        }


        log.info("initDutyInfo Done............");

    }


    @Transactional
    public void scheduling(DutySettingDto param){

        if (param.getDutyId() != null
            && param.getUserIds() != null
            && param.getUserIds().size() > 0){
            if (dutyUserDao.existsByDutyId(param.getDutyId())){
                dutyUserDao.deleteAllByDutyId(param.getDutyId());
            }
            List<DutyUser> dutyUsers = new ArrayList<>();
            param.getUserIds().forEach(uid->{
                DutyUser du = new DutyUser(null,param.getDutyId(),uid);
                dutyUsers.add(du);
            });

            if (dutyUsers.size() > 0){
                dutyUserDao.saveAll(dutyUsers);
            }

        }

    }


    public static void main(String... args) throws ParseException {
        List<DutyInfo> dutyInfos = calDutyPlan(new Date(),FORMATOR_DATE_TIME.parse("2022-7-5 14:00:05"));
        for (DutyInfo dutyInfo : dutyInfos) {
            log.info("日期=" + dutyInfo.getDutyDate());
            log.info("开始时间=" + FORMATOR_DATE_TIME.format(dutyInfo.getStartTime()));
            log.info("结束时间=" + FORMATOR_DATE_TIME.format(dutyInfo.getEndTime()));
        }

    }


    /**
     * 查询某一排班的值班人员
     * @param dutyId
     */
    public  List<MyUser> findDutyUser(Long dutyId) {
        List<DutyUser> dutyUsers = dutyUserDao.findByDutyId(dutyId);

        if (dutyUsers != null && dutyUsers.size() > 0){
            List<MyUser> users = userDao.findAllByIdIn(dutyUsers.stream().map(DutyUser::getUserId).collect(Collectors.toList()));
            return users;
        }
        return null;
    }
}
