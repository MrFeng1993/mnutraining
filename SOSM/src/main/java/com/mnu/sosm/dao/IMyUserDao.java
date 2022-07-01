package com.mnu.sosm.dao;

import com.mnu.sosm.entity.MyUser;
import com.mnu.sosm.repository.ICustomRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IMyUserDao extends ICustomRepository<MyUser,Long> {

    MyUser findByAccount(String username);

    boolean existsByRoleIdAndStatus(Long roleId, Byte status);

    boolean existsByAccountAndStatus(String account, Byte status);


    boolean existsByAccountAndStatusAndIdIsNot(String account, Byte status,Long id);


    List<MyUser> findAllByIdIn(List<Long> ids);


    List<MyUser> findAllByStatus(Byte status);
}
