package org.example.dzcsmq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@MapperScan("org.example.dzcsmq.mqserver.mapper")
@SpringBootApplication
public class DzcsmqApplication {
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(DzcsmqApplication.class, args);
    }

}
