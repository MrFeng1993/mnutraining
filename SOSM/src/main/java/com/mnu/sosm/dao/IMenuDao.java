package com.mnu.sosm.dao;

import com.mnu.sosm.entity.Menu;
import com.mnu.sosm.repository.ICustomRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMenuDao extends ICustomRepository<Menu,Long> {


    @Query(value = "from Menu where id in :ids")
    List<Menu> findByIdIn(@Param("ids")List<Long> ids);

}
