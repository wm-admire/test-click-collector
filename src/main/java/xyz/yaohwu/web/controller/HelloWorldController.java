package xyz.yaohwu.web.controller;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.yaohwu.spider.jira.ConfigException;
import xyz.yaohwu.spider.jira.issue.JiraTestIssueConfig;
import xyz.yaohwu.spider.jira.issue.JiraTestIssueSpider;
import xyz.yaohwu.web.pojo.HelloWorld;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yaohwu
 */
@RestController
@RequestMapping("/{version}/hello/")
public class HelloWorldController {


    private static final String TEMPLATE = "Hello, %s!";
    private static final String SEPARATOR = ";";
    private final AtomicLong counter = new AtomicLong();


    @RequestMapping("/demo")
    public HelloWorld helloWorld(@RequestParam(value = "name", defaultValue = "World") String name, @PathVariable String version) {
        return new HelloWorld(counter.incrementAndGet(),
                String.format(TEMPLATE, name));
    }

    @RequestMapping("/search")
    public void search(@RequestParam(value = "username") String username,
                       @RequestParam(value = "password") String password,
                       @RequestParam(value = "users") String users,
                       @PathVariable String version) {
        if (username == null) {
            throw new ConfigException("username is null");
        }
        if (password == null) {
            throw new ConfigException("password is null");
        }
        if (users == null) {
            throw new ConfigException("users is null");
        }
        JiraTestIssueConfig.getInstance().setUsername(username);
        JiraTestIssueConfig.getInstance().setPassword(password);
        JiraTestIssueConfig.getInstance().clearUser();
        for (String s : users.split(SEPARATOR)) {
            JiraTestIssueConfig.getInstance().addUser(s);
        }
        JiraTestIssueSpider.getInstance().crab();
    }

}
