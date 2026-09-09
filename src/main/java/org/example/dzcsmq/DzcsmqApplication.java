package org.example.dzcsmq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.example.dzcsmq.mqserver.mapper")
@SpringBootApplication
public class DzcsmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(DzcsmqApplication.class, args);
    }

}
