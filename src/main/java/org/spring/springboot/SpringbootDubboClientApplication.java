package org.spring.springboot;

import com.github.pagehelper.PageHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication

public class SpringbootDubboClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDubboClientApplication.class, args);
    }

}

