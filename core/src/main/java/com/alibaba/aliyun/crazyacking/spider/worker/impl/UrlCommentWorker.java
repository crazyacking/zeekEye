package com.alibaba.aliyun.crazyacking.spider.worker.impl;

import com.alibaba.aliyun.crazyacking.spider.fetcher.CommentFetcher;
import com.alibaba.aliyun.crazyacking.spider.handler.NextUrlHandler;
import com.alibaba.aliyun.crazyacking.spider.parser.CommentParser;
import com.alibaba.aliyun.crazyacking.spider.parser.bean.Account;
import com.alibaba.aliyun.crazyacking.spider.queue.AccountQueue;
import com.alibaba.aliyun.crazyacking.spider.queue.CommentUrlQueue;
import com.alibaba.aliyun.crazyacking.spider.utils.Initializer;
import com.alibaba.aliyun.crazyacking.spider.utils.Utils;
import com.alibaba.aliyun.crazyacking.spider.worker.BasicWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * 从UrlQueue中取出url，下载页面，分析url，保存已访问rul
 *
 * @author crazyacking
 */
public class UrlCommentWorker extends BasicWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(UrlCommentWorker.class.getName());

    @Resource(name = "initializer")
    Initializer initializer;

    /**
     * 下载对应页面并分析出页面对应URL，放置在未访问队列中
     *
     * @param url 返回值：被封账号/系统繁忙/OK
     */
    protected String dataHandler(String url) {
        return NextUrlHandler.addNextCommentUrl(CommentFetcher.getContentFromUrl(url));
    }

    @Override
    public void run() {
        // 首先获取账号并登录
        Account account = AccountQueue.outElement();
        AccountQueue.addElement(account);
        this.username = account.getUsername();
        this.password = account.getPassword();

        // 使用账号登录
        String gsid = login(username, password);
        String result;
        try {
            // 若登录失败，则执行一轮切换账户的操作，如果还失败，则退出
            if (gsid == null) {
                gsid = switchAccount();
            }

            // 登录成功
            if (gsid != null) {
                // 当URL队列不为空时，从未访问队列中取出url进行分析
                while (!CommentUrlQueue.isEmpty()) {
                    // 从队列中获取URL并处理
                    result = dataHandler(CommentUrlQueue.outElement() + "&" + gsid);

                    // 针对处理结果进行处理：OK, SYSTEM_BUSY, ACCOUNT_FORBIDDEN
                    gsid = process(result, gsid);

                    // 没有新的URL了，从数据库中继续拿一个
                    if (CommentUrlQueue.isEmpty()) {
                        // 仍为空，从数据库中取
                        if (CommentUrlQueue.isEmpty()) {
                            logger.info(">> Add new comment Url...");
                            initializer.initializeCommentUrl();

                            // 拿完还是空，退出爬虫
                            if (CommentUrlQueue.isEmpty()) {
                                logger.info(">> All comments of all weibos have been fetched...");
                                break;
                            }
                        }
                    }
                }
            } else {
                logger.info(">> " + username + " login failed!");
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

        // 关闭数据库连接
        try {
            CommentParser.conn.close();
            Utils.conn.close();
        } catch (SQLException e) {
            logger.error(e.toString());
        }

        logger.info("Spider stop...");
    }
}
