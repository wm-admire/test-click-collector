package xyz.yaohwu.spider.jira;

/**
 * 登录异常
 *
 * @author yaohwu
 */
public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}
