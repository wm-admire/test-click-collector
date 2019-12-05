package xyz.yaohwu.spider.jira.issue;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yaohwu.bot.BotAlert;
import xyz.yaohwu.spider.Spider;
import xyz.yaohwu.spider.jira.JQLParser;
import xyz.yaohwu.spider.jira.LoginException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yaohwu
 */
public class JiraTestIssueSpider implements Spider {

    private static final Logger logger = LoggerFactory.getLogger(JiraTestIssueSpider.class);

    private static final String TEST_COLLECTOR_JQL =
            "project in (报表, 代码任务, 移动端, BI开发, 决策平台) " +
                    "AND " +
                    "(" +
                    "status changed by currentUser() from 研发组员问题解决中 to 测试组长分配 during (startOfWeek(), endOfWeek()) " +
                    "OR " +
                    "status changed by currentUser() from 研发组员问题解决中 to 研发组长验收 during (startOfWeek(), endOfWeek())" +
                    ") " +
                    "AND 是否针对该问题补充了单元测试 != EMPTY " +
                    "AND worklogAuthor in (membersOf(D-研发-功能-报表), membersOf(D-研发-引擎-报表))";

    private static final String JIRA_URL = "http://www.finedevelop.com:2016/rest/api/2/search/";
    private static final String JIRA_LOGIN = "http://www.finedevelop.com:2016/login.jsp";

    private PoolingHttpClientConnectionManager manager;
    private CookieStore cookieStore;


    private JiraTestIssueSpider() {
        manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(50);
        manager.setDefaultMaxPerRoute(20);
        cookieStore = new BasicCookieStore();
    }

    private static class Holder {
        private static final Spider INSTANCE = new JiraTestIssueSpider();
    }

    public static Spider getInstance() {
        return Holder.INSTANCE;
    }


    @Override
    public void crab() {
        login();
        TestIssueResult result = search();
        BotAlert.getInstance().alert(result.toString());
    }

    private CloseableHttpClient buildClient() {
        return HttpClients.custom().setConnectionManager(manager).setDefaultCookieStore(cookieStore).build();
    }


    /**
     * 登录
     */
    private void login() {

        CloseableHttpClient client = buildClient();
        HttpPost post = new HttpPost(JIRA_LOGIN);
        List<NameValuePair> pairs = new ArrayList<>();

        pairs.add(new BasicNameValuePair("os_username", JiraTestIssueConfig.getInstance().getUsername()));
        pairs.add(new BasicNameValuePair("os_password", JiraTestIssueConfig.getInstance().getPassword()));
        pairs.add(new BasicNameValuePair("os_cookie", "true"));
        pairs.add(new BasicNameValuePair("os_destination", ""));
        pairs.add(new BasicNameValuePair("user_role", ""));
        pairs.add(new BasicNameValuePair("atl_token", ""));
        pairs.add(new BasicNameValuePair("login", "登录"));

        post.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8));

        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            if (302 != response.getStatusLine().getStatusCode()) {
                throw new LoginException(response.getStatusLine().toString() + ": " + EntityUtils.toString(response.getEntity()));
            }
        } catch (IOException e) {
            throw new LoginException(e.getMessage());
        }
    }

    /**
     * 搜索
     */
    private TestIssueResult search() {
        TestIssueResult results = new TestIssueResult("近一周报表任务单元测试未覆盖个数：");
        CloseableHttpClient collectorClient = HttpClients.custom().setConnectionManager(manager).setDefaultCookieStore(cookieStore).build();
        Set<String> users = JiraTestIssueConfig.getInstance().getUsers();
        for (String user : users) {
            HttpPost post = new HttpPost(JIRA_URL);
            post.setEntity(buildEntity(user));
            addHeader(post);
            JSONObject result = null;
            try {
                CloseableHttpResponse response = collectorClient.execute(post);
                result = new JSONObject(EntityUtils.toString(response.getEntity()));
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            if (result != null) {
                Integer score = result.getInt("total");
                results.add(user, score);
            }
        }
        return results;
    }

    private StringEntity buildEntity(String user) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startAt", 0);
        jsonObject.put("jql", JQLParser.USERNAME.parser(TEST_COLLECTOR_JQL, user));
        jsonObject.put("maxResults", 200);
        jsonObject.put("fields", new JSONArray().put("id").put("key"));
        return new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
    }

    private void addHeader(HttpPost post) {
        post.addHeader("Accept", "application/json");
        post.addHeader("Accept-Encoding", "gzip, deflate");
        post.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,vi;q=0.6,ja;q=0.5,und;q=0.4");
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Host", "www.finedevelop.com:2016");
        post.addHeader("Origin", "http://www.finedevelop.com:2016");
        post.addHeader("Referer", "http://www.finedevelop.com:2016/issues/?jql=");
        post.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36");
        post.addHeader("Cache-Control", "no-cache");
        post.addHeader("Pragma", "no-cache");
        post.addHeader("X-Requested-With", "XMLHttpRequest");
    }
}
