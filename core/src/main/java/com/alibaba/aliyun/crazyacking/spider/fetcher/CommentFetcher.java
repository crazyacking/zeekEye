package com.alibaba.aliyun.crazyacking.spider.fetcher;

import com.alibaba.aliyun.crazyacking.spider.parser.CommentParser;
import com.alibaba.aliyun.crazyacking.spider.parser.bean.Page;
import com.alibaba.aliyun.crazyacking.spider.queue.CommentUrlQueue;
import com.alibaba.aliyun.crazyacking.spider.queue.VisitedCommentUrlQueue;
import com.alibaba.aliyun.crazyacking.spider.utils.Constants;
import com.alibaba.aliyun.crazyacking.spider.utils.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommentFetcher {
    private static final Logger logger = LoggerFactory.getLogger(CommentFetcher.class.getName());

    /**
     * @param url
     * @return
     */
    public static Page getContentFromUrl(String url) {
        String content = null;
        Document contentDoc = null;

        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
        HttpConnectionParams.setSoTimeout(params, 10 * 1000);
        AbstractHttpClient httpClient = new DefaultHttpClient(params);
        HttpGet getHttp = new HttpGet(url);
        getHttp.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
        HttpResponse response;

        try {
            response = httpClient.execute(getHttp);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // ת��Ϊ�ı���Ϣ, ������ȡ��ҳ���ַ�������ֹ����
                content = EntityUtils.toString(entity, "UTF-8");

                String returnMsg = Utils.checkContent(content, url, FetcherType.COMMENT);
                if (returnMsg != null) {
                    return new Page(returnMsg, null);
                }

                // ��content�ַ���ת����Document����
                contentDoc = CommentParser.getPageDocument(content);
                // ȡ�����ҳ�����е�����
                List<Element> commentItems = CommentParser.getGoalContent(contentDoc);
                if (commentItems != null && commentItems.size() > 0) {
                    CommentParser.createFile(commentItems, url);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());

            // ����ʱ��������æ��ͬ
            url = url.split("&gsid")[0];
            logger.info(">> Put back url: " + url);
            CommentUrlQueue.addFirstElement(url);
            return new Page(Constants.SYSTEM_BUSY, null);
        }

        VisitedCommentUrlQueue.addElement(url);

        return new Page(content, contentDoc);
    }
}

