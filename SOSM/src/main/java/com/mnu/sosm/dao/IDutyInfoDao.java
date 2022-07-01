package com.mnu.sosm.dao;

import com.mnu.sosm.entity.DutyInfo;
import com.mnu.sosm.entity.Menu;
import com.mnu.sosm.repository.ICustomRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IDutyInfoDao extends ICustomRepository<DutyInfo,Long> {


    /**
     * 查询最后一个班次
     * @return
     */
    @Query(nativeQuery = true,value = "select d.* from mnu_sosm_duty d where" +
            " d.daily_order = 2 order by start_time desc limit 1")
    DutyInfo findLatestDuty();
}
