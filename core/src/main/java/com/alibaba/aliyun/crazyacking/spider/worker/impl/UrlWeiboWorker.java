package com.alibaba.aliyun.crazyacking.spider.worker.impl;

import com.alibaba.aliyun.crazyacking.spider.common.Initializer;
import com.alibaba.aliyun.crazyacking.spider.common.Utils;
import com.alibaba.aliyun.crazyacking.spider.fetcher.WeiboFetcher;
import com.alibaba.aliyun.crazyacking.spider.handler.NextUrlHandler;
import com.alibaba.aliyun.crazyacking.spider.parser.WeiboParser;
import com.alibaba.aliyun.crazyacking.spider.parser.bean.Account;
import com.alibaba.aliyun.crazyacking.spider.queue.AccountQueue;
import com.alibaba.aliyun.crazyacking.spider.queue.WeiboUrlQueue;
import com.alibaba.aliyun.crazyacking.spider.worker.BasicWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * 从UrlQueue中取出url，下载页面，分析url，保存已访问rul
 * author:crazyacking
 */
public class UrlWeiboWorker extends BasicWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(UrlWeiboWorker.class.getName());
    @Resource(name = "initializer")
    Initializer initializer;

    /**
     * 下载对应页面并分析出页面对应URL，放置在未访问队列中
     *
     * @param url 返回值：被封账号/系统繁忙/OK
     */
    protected String dataHandler(String url) {
        return NextUrlHandler.addNextWeiboUrl(WeiboFetcher.getContentFromUrl(url));
    }

    @Override
    public void run() {
        // 首先获取账号并登录
        logger.info("启动爬虫线程...");
        //获取一个登录账号
        Account account = AccountQueue.outElement();
        AccountQueue.addElement(account);
        this.username = account.getUserName();
        this.password = account.getPassword();

        // 使用账号登录，并获取gsid

        String gsid = login(username, password);
        String result;
        try {
            // 若登录失败，则执行一轮切换账户的操作，如果还失败，则退出
            if (gsid == null) {
                gsid = switchAccount();
                logger.info("微博登录失败，正在切换账号...");
                Thread.sleep(1000);
            }

            // 登录成功
            if (gsid != null) {
                logger.info("微博登录成功，开始获取gsid码...");
                Thread.sleep(1000);
                // 当URL队列不为空时，从未访问队列中取出url进行分析
                while (!WeiboUrlQueue.isEmpty()) {
                    // 从队列中获取URL并处理
                    result = dataHandler(WeiboUrlQueue.outElement() + "&" + gsid);
                    logger.info("System " + result + ".");

                    // 针对处理结果进行处理：OK, SYSTEM_BUSY, ACCOUNT_FORBIDDEN
                    gsid = process(result, gsid);

                    logger.info(gsid);

                    // 没有新的URL了，从数据库中继续拿一个
                    if (WeiboUrlQueue.isEmpty()) {
                        // 仍为空，从数据库中取
                        if (WeiboUrlQueue.isEmpty()) {
                            logger.info(">> Add new weibo Url...");
                            logger.info(">> Add new weibo Url...");
                            initializer.initWeiboUrl();

                            // 拿完还是空，退出爬虫
                            if (WeiboUrlQueue.isEmpty()) {
                                logger.info(">> All users have been fetched...");
                                logger.info(">> All users have been fetched...");
                                break;
                            }
                        }
                    }
                }
            } else {
                logger.info(username + " login failed!");
                logger.info(username + " login failed!");
            }

        } catch (Exception e) {
            logger.error("", e);
        }
        try {
            WeiboParser.conn.close();
            Utils.conn.close();
        } catch (SQLException e) {
            logger.error("", e);
        }
        logger.info("Spider stop...");
        logger.info("Spider stop...");
    }

}
