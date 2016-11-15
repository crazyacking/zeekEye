package cn.edu.hut.crazyacking.spider.fetcher;

import cn.edu.hut.crazyacking.spider.common.Constants;
import cn.edu.hut.crazyacking.spider.common.Utils;
import cn.edu.hut.crazyacking.spider.parser.FollowParser;
import cn.edu.hut.crazyacking.spider.parser.bean.Page;
import cn.edu.hut.crazyacking.spider.queue.VisitedFollowUrlQueue;
import cn.edu.hut.crazyacking.spider.queue.FollowUrlQueue;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FolloweeFetcher {
    private static final Logger logger = LoggerFactory.getLogger(FolloweeFetcher.class.getName());

    public static Document getPageDocument(String content) {
        return Jsoup.parse(content);
    }

    /**
     * 根据url爬取网页内容
     *
     * @param url
     * @return
     */
    public static Page getContentFromUrl(String url, CookieStore cookie, int currentLevel) {
        String content = null;
        Document contentDoc = null;

        // 设置GET超时时间
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
        HttpConnectionParams.setSoTimeout(params, 10 * 1000);

        AbstractHttpClient httpClient = new DefaultHttpClient(params);
        httpClient.setCookieStore(cookie);

        HttpGet getHttp = new HttpGet(url);

        // 设置HTTP Header
        getHttp.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
        HttpResponse response;

        try {
            // 获得信息载体
            response = httpClient.execute(getHttp);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // 转化为文本信息, 设置爬取网页的字符集，防止乱码
                content = EntityUtils.toString(entity, "UTF-8");

                String returnMsg = Utils.checkContent(content, url, FetcherType.FOLLOW);
                if (returnMsg != null) {
                    return new Page(returnMsg, null);
                }

                // 将content字符串转换成Document对象
                contentDoc = FollowParser.getPageDocument(content);

                //取回这个页面所有的followee
                List<Element> followeeItems = FollowParser.getGoalContent(contentDoc);
                if (followeeItems != null && followeeItems.size() > 0) {
                    FollowParser.createFile(followeeItems, url, currentLevel);
                }
            }
        } catch (Exception e) {
            logger.error("", e);

            /*
            处理超时，和请求忙相同
             */
            logger.info(">> Put back url: " + url);
            FollowUrlQueue.addFirstElement(url);
            return new Page(Constants.SYSTEM_BUSY, null);
        }

        VisitedFollowUrlQueue.addElement(url);

        return new Page(content, contentDoc);
    }
}

