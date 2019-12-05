package xyz.yaohwu.spider.jira.issue;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaohwu
 */
public class TestIssueResult {
    private String detail;
    private Map<String, Integer> result = new HashMap<>();

    public TestIssueResult(String detail) {
        this.detail = detail;
    }

    public void add(String user, Integer score) {
        result.put(user, score);
    }

    @Override
    public String toString() {
        List<Map.Entry<String, Integer>> content = new ArrayList<>(result.entrySet());
        content.sort(Map.Entry.comparingByValue());
        StringBuilder builder = new StringBuilder(detail);
        for (Map.Entry<String, Integer> stringIntegerEntry : content) {
            builder.append("\n")
                    .append(stringIntegerEntry.getKey())
                    .append(": ")
                    .append(stringIntegerEntry.getValue());
        }
        builder.append("\n");
        builder.append("more info: http://www.finedevelop.com:2016/secure/Dashboard.jspa?selectPageId=14650");
        return builder.toString();
    }
}
