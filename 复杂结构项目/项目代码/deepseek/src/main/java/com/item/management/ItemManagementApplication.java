// ItemManagementApplication.java - Spring Boot主启动类
package com.item.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ItemManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemManagementApplication.class, args);
    }
}