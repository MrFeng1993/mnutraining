package com.mnu.sosm;

import com.mnu.sosm.dao.IMenuDao;
import com.mnu.sosm.dao.IMyRoleDao;
import com.mnu.sosm.dao.IMyUserDao;
import com.mnu.sosm.dao.IRoleMenuDao;
import com.mnu.sosm.entity.Menu;
import com.mnu.sosm.entity.MyRole;
import com.mnu.sosm.entity.MyUser;
import com.mnu.sosm.entity.RoleMenu;
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



    @Override
    public void afterPropertiesSet() throws Exception {


        try {

            log.info("开始系统初始化......");

            log.info("系统初始化完成");

        } catch (Exception e) {
            log.error("初始化错误",e);
        }
    }

}
