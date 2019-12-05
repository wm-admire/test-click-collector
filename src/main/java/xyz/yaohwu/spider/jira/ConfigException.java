package xyz.yaohwu.spider.jira;

/**
 * @author yaohwu
 */
public class ConfigException extends RuntimeException {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable e) {
        super(message, e);
    }
}
