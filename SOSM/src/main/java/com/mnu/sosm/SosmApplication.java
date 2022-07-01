package com.mnu.sosm;

import com.mnu.sosm.repository.impl.CustomRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
@EnableScheduling //开启定时任务注解
public class SosmApplication {

    public static void main(String[] args) {
        SpringApplication.run(SosmApplication.class, args);
    }

}
