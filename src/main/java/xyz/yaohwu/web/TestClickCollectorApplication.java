package xyz.yaohwu.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yaohwu
 */
@SpringBootApplication(scanBasePackages = {"xyz.yaohwu.web"})
public class TestClickCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestClickCollectorApplication.class, args);
    }

}
