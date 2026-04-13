package cn.detect.cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching
public class CsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CsApplication.class, args);
    }
}