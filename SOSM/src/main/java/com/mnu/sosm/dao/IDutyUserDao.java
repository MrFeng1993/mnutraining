package com.mnu.sosm.dao;

import com.mnu.sosm.entity.DutyUser;
import com.mnu.sosm.entity.RoleMenu;
import com.mnu.sosm.repository.ICustomRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IDutyUserDao extends ICustomRepository<DutyUser,Long> {


    boolean existsByDutyId(Long dutyId);

    //JPQL
    @Query(value = "from DutyUser where dutyId = :dutyId")
    List<DutyUser> findByDutyId(Long dutyId);

    @Modifying
    @Query(value = "delete from DutyUser where dutyId = :dutyId")
    void deleteAllByDutyId(Long dutyId);
}
