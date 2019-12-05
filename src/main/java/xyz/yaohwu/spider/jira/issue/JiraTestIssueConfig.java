package xyz.yaohwu.spider.jira.issue;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author yaohwu
 */
public class JiraTestIssueConfig {

    private static final String SEPARATOR = ";";

    private static class Holder {
        private static final JiraTestIssueConfig INSTANCE = new JiraTestIssueConfig();
    }

    private JiraTestIssueConfig() {
    }

    public static JiraTestIssueConfig getInstance() {
        return Holder.INSTANCE;
    }

    private String username = "";
    private String password = "";
    private Set<String> users = new CopyOnWriteArraySet<>();

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

    public Set<String> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public void addUser(String user) {
        this.users.add(user);
    }

    public void clearUser() {
        this.users.clear();
    }
}