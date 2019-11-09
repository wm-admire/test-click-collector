package xyz.yaohwu.web.controller;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.yaohwu.spider.jira.JiraSpider;
import xyz.yaohwu.web.pojo.HelloWorld;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yaohwu
 */
@RestController
@RequestMapping("/{version}/hello/")
public class HelloWorldController {


    private static final String TEMPLATE = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();


    @RequestMapping("/demo")
    public HelloWorld helloWorld(@RequestParam(value = "name", defaultValue = "World") String name, @PathVariable String version) {
        return new HelloWorld(counter.incrementAndGet(),
                String.format(TEMPLATE, name));
    }

    @RequestMapping("/search")
    public void search(@RequestParam(value = "name", defaultValue = "World") String name, @PathVariable String version) {
        JiraSpider.getInstance().crab();
    }

}
