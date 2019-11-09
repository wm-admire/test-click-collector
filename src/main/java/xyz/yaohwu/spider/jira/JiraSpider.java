package xyz.yaohwu.spider.jira;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yaohwu.spider.Spider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yaohwu
 */
public class JiraSpider implements Spider {

    private static final String USER_NAME = "${user.name}";

    private static final String TEST_COLLECTOR_JQL = "worklogAuthor = ${user.name} AND " +
            "(status changed FROM 研发组员问题解决中 to 测试组长分配 DURING (startOfDay(),endOfDay()) " +
            "or status changed  from 研发组员问题解决中 to 研发组长验收 DURING (startOfDay(),endOfDay())) " +
            "and 是否针对该问题补充了单元测试 = 否";

    private static final String JIRA_URL = "http://www.finedevelop.com:2016/rest/issueNav/1/issueTable";
    private static final String JIRA_LOGIN = "http://www.finedevelop.com:2016/login.jsp";

    private PoolingHttpClientConnectionManager manager;
    private CookieStore cookieStore;
    private static final Logger logger = LoggerFactory.getLogger(JiraSpider.class);


    private JiraSpider() {
        manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(50);
        manager.setDefaultMaxPerRoute(20);
        cookieStore = new BasicCookieStore();
    }

    private static class Holder {
        private static final Spider INSTANCE = new JiraSpider();
    }

    public static Spider getInstance() {
        return Holder.INSTANCE;
    }


    @Override
    public void crab() {
        JiraConfig.getInstance().setUsername("******");
        JiraConfig.getInstance().setPassword("******");
        JiraConfig.getInstance().addUser("yaoh.wu");
        login();
        search();
    }

    private CloseableHttpClient buildClient() {
        return HttpClients.custom().setConnectionManager(manager).setDefaultCookieStore(cookieStore).build();
    }


    private void login() {

        CloseableHttpClient client = buildClient();
        HttpPost post = new HttpPost(JIRA_LOGIN);
        List<NameValuePair> pairs = new ArrayList<>();

        pairs.add(new BasicNameValuePair("os_username", JiraConfig.getInstance().getUsername()));
        pairs.add(new BasicNameValuePair("os_password", JiraConfig.getInstance().getPassword()));
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

    private void search() {
        CloseableHttpClient collectorClient = HttpClients.custom().setConnectionManager(manager).setDefaultCookieStore(cookieStore).build();

        List<String> users = JiraConfig.getInstance().getUsers();
        for (String user : users) {
            HttpPost post = new HttpPost(JIRA_URL);
            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("startIndex", "0"));
            pairs.add(new BasicNameValuePair("jql", TEST_COLLECTOR_JQL.replace(USER_NAME, user)));
            pairs.add(new BasicNameValuePair("layoutKey", "list-view"));
            post.addHeader("__amdModuleName", "jira/issue/utils/xsrf-token-header");
            post.addHeader("Accept", "*/*");
            post.addHeader("Accept-Encoding", "gzip, deflate");
            post.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,vi;q=0.6,ja;q=0.5,und;q=0.4");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            post.addHeader("Host", "www.finedevelop.com:2016");
            post.addHeader("Origin", "http://www.finedevelop.com:2016");
            post.addHeader("Referer", "http://www.finedevelop.com:2016/issues/?jql=worklogAuthor%20%3D%20yaoh.wu%20AND%20(status%20changed%20FROM%20%E7%A0%94%E5%8F%91%E7%BB%84%E5%91%98%E9%97%AE%E9%A2%98%E8%A7%A3%E5%86%B3%E4%B8%AD%20to%20%E6%B5%8B%E8%AF%95%E7%BB%84%E9%95%BF%E5%88%86%E9%85%8D%20DURING%20(startOfDay()%2CendOfDay())%20or%20status%20changed%20%20from%20%E7%A0%94%E5%8F%91%E7%BB%84%E5%91%98%E9%97%AE%E9%A2%98%E8%A7%A3%E5%86%B3%E4%B8%AD%20to%20%E7%A0%94%E5%8F%91%E7%BB%84%E9%95%BF%E9%AA%8C%E6%94%B6%20DURING%20(startOfDay()%2CendOfDay()))%20and%20%E6%98%AF%E5%90%A6%E9%92%88%E5%AF%B9%E8%AF%A5%E9%97%AE%E9%A2%98%E8%A1%A5%E5%85%85%E4%BA%86%E5%8D%95%E5%85%83%E6%B5%8B%E8%AF%95%20%3D%20%E5%90%A6");
            post.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36");
            post.addHeader("X-Atlassian-Token", "no-check");
            post.addHeader("X-Requested-With", "XMLHttpRequest");
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
            post.setEntity(encodedFormEntity);

            try {
                CloseableHttpResponse response = collectorClient.execute(post);
                String result = EntityUtils.toString(response.getEntity());
                EntityUtils.consume(response.getEntity());
                logger.info(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
