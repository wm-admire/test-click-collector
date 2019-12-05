package xyz.yaohwu.bot;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author yaohwu
 */
public class BotAlert {

    private static final Logger logger = LoggerFactory.getLogger(BotAlert.class);

    private PoolingHttpClientConnectionManager manager;

    public static final String BOT_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=4c8aada2-b56e-4587-b18f-4ed561e9f763";

    private BotAlert() {
        manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(50);
        manager.setDefaultMaxPerRoute(20);
    }

    private static class Holder {
        private static final BotAlert INSTANCE = new BotAlert();
    }

    public static BotAlert getInstance() {
        return Holder.INSTANCE;
    }

    public void alert(String content) {
        CloseableHttpClient client = buildClient();
        HttpPost post = new HttpPost(BOT_URL);
        /*
         * {
         *    "msgtype": "text",
         *    "text": {
         *    "content": "广州今日天气：29度，大部分多云，降雨概率：60%",
         *    "mentioned_list":["wangqing","@all"],
         *    "mentioned_mobile_list":["13800001111","@all"]
         *    }
         * }
         * */
        JSONObject data = new JSONObject();
        data.put("msgtype", "text");
        data.put("text", new JSONObject().put("content", content));
        post.setEntity(new StringEntity(data.toString(), ContentType.APPLICATION_JSON));
        try {
            client.execute(post);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private CloseableHttpClient buildClient() {
        return HttpClients.custom().setConnectionManager(manager).build();
    }


}
