import com.alibaba.aliyun.crazyacking.spider.utils.Initializer;
import com.alibaba.aliyun.crazyacking.spider.utils.Utils;
import com.alibaba.aliyun.crazyacking.spider.worker.impl.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/*
* Copyright (c) 2015 crazyacking All rights reserved.
* Author: crazyacking
* Created Date: 2015/3/25
*/

public class WeiboSpiderStarter {

    private static final Logger logger = LoggerFactory.getLogger(WeiboSpiderStarter.class);
    private static final int WORKER_NUM = 5;
    private static final String TYPE = null;

    @Resource(name = "initializer")
    Initializer initializer;

    private void fetchAbnormalWeibo() {
        // 初始化账号队列
        Utils.readAccountFromFile();

        // 初始化微博页面链接
        Initializer.initializeAbnormalWeiboUrl();

        // 启动爬虫worker线程
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new UrlAbnormalWorker()).start();
        }
    }

    private void fetchComment() {
        // 初始化账号队列
        Utils.readAccountFromFile();

        // 初始化评论页面链接
        Initializer.initializeCommentUrl();

        // 启动爬虫worker线程
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new UrlCommentWorker()).start();
        }
    }

    private void fetchRepost() {
        // 初始化账号队列
        Utils.readAccountFromFile();

        // 初始化转发页面链接
        Initializer.initializeRepostUrl();

        // 启动爬虫worker线程
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new UrlRepostWorker()).start();
        }
    }

    private void fetchFollowee() {
        // 初始化账号队列
        Utils.readAccountFromFile();

        // 初始化关注页面链接
        UrlFollowWorker.CURRENT_LEVEL = Initializer.initializeFollowUrl();

        // 启动爬虫worker线程
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new UrlFollowWorker()).start();
        }
    }

    private void fetchWeibo() {
        // 初始化账号队列
        /*
        * 把文件中的账号读入到AccountQueue中
		* */
        Utils.readAccountFromFile();

        /*
        * 从数据库中取出待爬取用户ID，构造初始用户粉丝页面的Url，并放入待爬取队列WeiboUrlQueue
        * 初始化微博页面链接
		* */
        Initializer.initializeWeiboUrl();

        // 启动爬虫worker线程
        for (int i = 0; i < WORKER_NUM; i++) {
            new Thread(new UrlWeiboWorker()).start();
        }
    }

    @Test
    public void startSpider() {

        // 初始化配置参数
        initializer.initializeParams();

        // 根据type判断爬虫任务类型
        if (TYPE.equals("weibo")) {
            fetchWeibo();
        } else if (TYPE.equals("comment")) {
            fetchComment();
        } else if (TYPE.equals("repost")) {
            fetchRepost();
        } else if (TYPE.equals("abnormal")) {
            fetchAbnormalWeibo();
        } else if (TYPE.equals("follow")) {
            fetchFollowee();
        } else {
            logger.error("Unknown crawl type: " + TYPE + ".\n Exit...");
        }

    }

}
