package com.alibaba.aliyun.crazyacking.spider.worker.impl;

import com.alibaba.aliyun.crazyacking.spider.fetcher.FolloweeFetcher;
import com.alibaba.aliyun.crazyacking.spider.handler.NextUrlHandler;
import com.alibaba.aliyun.crazyacking.spider.parser.FollowParser;
import com.alibaba.aliyun.crazyacking.spider.parser.bean.Account;
import com.alibaba.aliyun.crazyacking.spider.queue.AccountQueue;
import com.alibaba.aliyun.crazyacking.spider.queue.FollowUrlQueue;
import com.alibaba.aliyun.crazyacking.spider.utils.Initializer;
import com.alibaba.aliyun.crazyacking.spider.utils.Utils;
import com.alibaba.aliyun.crazyacking.spider.worker.BasicWorker;
import org.apache.http.client.CookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * 从UrlQueue中取出url，下载页面，分析url，保存已访问url
 *
 * @author crazyacking
 */
public class UrlFollowWorker extends BasicWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(UrlFollowWorker.class.getName());
    public static int CURRENT_LEVEL = 0;
    @Resource(name = "initializer")
    Initializer initializer;

    /**
     * 下载对应页面并分析出页面对应URL，放置在未访问队列中
     *
     * @param url 返回值：被封账号/系统繁忙/OK
     */
    protected String dataHandler(String url) {
        return null;
    }

    private String dataHandler(String url, CookieStore cookie, int currentLevel) {
        return NextUrlHandler.addNextFollowUrl(FolloweeFetcher.getContentFromUrl(url, cookie, currentLevel));
    }

    @Override
    public void run() {
        // 首先获取账号并登录
        Account account = AccountQueue.outElement();
        AccountQueue.addElement(account);
        this.username = account.getUsername();
        this.password = account.getPassword();

        // 使用账号登录
        CookieStore cookie = loginForCookie(username, password);
        String result;

        try {
            // 若登录失败，则执行一轮切换账户的操作，如果还失败，则退出
            if (cookie == null) {
                cookie = switchAccountForCookie();
            }

            // 登录成功
            if (cookie != null) {
//				int currentLevel = 0;

//				while(currentLevel < Constants.LEVEL) {
//					
//					// 添加该层的follower id列表URL
//					if(currentLevel == 0){
//						// 如果是第0层：从文件中初始化
//						Utils.initializeFollowUrl();
//					}
//					else {
//						// 如果其他层次：从数据库中获取上一层次的所有关注者ID，【做一下unique】
//						Utils.addNextLevelFollower(currentLevel);
//					}
//					
//					// 增加到本层次
//					currentLevel++;
//					
//					// 根据follower id URL进行逐个用户处理
//					while(!FollowUrlQueue.isEmpty()){
//						// 从队列中获取URL
//						String followUrl = FollowUrlQueue.outElement();
//						
//						// 获取这个ID对应的所有followee，并存入数据库
//						result = dataHandler(followUrl, cookie, currentLevel);
//					
//						// 针对处理结果进行处理：OK, SYSTEM_BUSY, ACCOUNT_FORBIDDEN
//						cookie = process(result, cookie);
//					}
//				}

//				logger.info(">> All followees of all followers have been fetched...");


                // 当URL队列不为空时，从未访问队列中取出url进行分析
                while (!FollowUrlQueue.isEmpty()) {

                    // 从队列中获取URL
                    String followUrl = FollowUrlQueue.outElement();

                    // 获取这个ID对应的所有followee，并存入数据库
                    result = dataHandler(followUrl, cookie, CURRENT_LEVEL);

                    // 针对处理结果进行处理：OK, SYSTEM_BUSY, ACCOUNT_FORBIDDEN
                    cookie = process(result, cookie);

                    // 没有新的URL了，从数据库中继续拿一个
                    if (FollowUrlQueue.isEmpty()) {

                        // 仍为空，从数据库中取
                        if (FollowUrlQueue.isEmpty()) {
                            logger.info(">> Add new follow Url...");
                            CURRENT_LEVEL = initializer.initializeFollowUrl();

                            // 拿完还是空，退出爬虫
                            if (FollowUrlQueue.isEmpty()) {
                                logger.info(">> All followees of all followers have been fetched...");
                                break;
                            }
                        }
                    }
                }


            } else {
                logger.info(username + " login failed!");
            }
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }

        // 关闭数据库连接
        try {
            FollowParser.conn.close();
            Utils.conn.close();
        } catch (SQLException e) {
            logger.error(e.toString());
        }

        logger.info("Spider stop...");
    }
}
