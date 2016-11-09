import com.alibaba.aliyun.crazyacking.spider.common.Initializer;
import com.alibaba.aliyun.crazyacking.spider.common.Utils;
import com.alibaba.aliyun.crazyacking.spider.worker.impl.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/*
* Author: crazyacking
* Date: 2015/3/25
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application.xml"})
public class SpiderStarter {
    private static final int WORKER_NUM = 5;
    private static final String TYPE = "WEIBO";

    @Resource(name = "initializer")
    Initializer initializer;

    @Test
    public void startSpider() {
        initializer.initializeParams();
        Utils.initAccount();

        switch (TYPE) {
            case "WEIBO":
                fetchWeibo();
                break;
            case "COMMENT":
                fetchComment();
                break;
            case "REPOST":
                fetchRepost();
                break;
            case "ABNORMAL":
                fetchAbnormalWeibo();
                break;
            case "FOLLOW":
                fetchFollowee();
                break;
        }
    }

    private void fetchAbnormalWeibo() {
        Utils.initAccount();
        Initializer.initAbnormalUrl();
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new AbnormalUrlWorker()).start();
        }
    }

    private void fetchComment() {
        Initializer.initCommentUrl();
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new UrlCommentWorker()).start();
        }
    }

    private void fetchRepost() {
        Initializer.initRepostUrl();
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new UrlRepostWorker()).start();
        }
    }

    private void fetchFollowee() {
        UrlFollowWorker.CURRENT_LEVEL = Initializer.initFollowUrl();
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new UrlFollowWorker()).start();
        }
    }

    private void fetchWeibo() {
        Initializer.initWeiboUrl();
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new UrlWeiboWorker()).start();
        }
    }

}
