package xyz.yaohwu.spider.jira;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yaohwu
 */
public class JiraConfig {

    private String username = "";
    private String password = "";
    private List<String> users = new CopyOnWriteArrayList<>();

    private static class Holder {
        private static JiraConfig CONFIG = new JiraConfig();
    }


    private JiraConfig() {
    }

    public static JiraConfig getInstance() {
        return Holder.CONFIG;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public void addUser(String user) {
        this.users.add(user);
    }
}
