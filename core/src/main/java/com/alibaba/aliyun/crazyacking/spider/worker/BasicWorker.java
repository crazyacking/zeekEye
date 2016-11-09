package com.alibaba.aliyun.crazyacking.spider.worker;

import com.alibaba.aliyun.crazyacking.spider.common.Constants;
import com.alibaba.aliyun.crazyacking.spider.common.Utils;
import com.alibaba.aliyun.crazyacking.spider.parser.LogType;
import com.alibaba.aliyun.crazyacking.spider.parser.bean.Account;
import com.alibaba.aliyun.crazyacking.spider.queue.AccountQueue;
import org.apache.http.client.CookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class BasicWorker {
    private static final Logger logger = LoggerFactory.getLogger(BasicWorker.class.getName());

    protected String username = null;
    protected String password = null;

    /**
     * 下载对应页面并分析出页面对应URL，放置在未访问队列中
     *
     * @param url 返回值：被封账号/系统繁忙/OK
     */
    protected abstract String dataHandler(String url);

    /**
     * 根据处理结果进行分析需要执行的动作，并返回合法的gsid
     *
     * @param result
     * @param gsid
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    protected String process(String result, String gsid) throws InterruptedException {
        // 处理成功，未被冻结账号，停顿200ms，防止频繁访问被快速冻结账号
        if (result.equals(Constants.OK)) {
            Thread.sleep(200);
        }
        // 系统繁忙
        else if (result.equals(Constants.SYSTEM_BUSY)) {
            logger.info("server busy, retry after 5s...");
            Thread.sleep(5 * 1000);
        }
        // 账号被冻结，切换账号继续执行
        else if (result.equals(Constants.ACCOUNT_FORBIDDEN)) {
            logger.info("Account has been frozen: {}", username);
            // 切换账号
            gsid = switchAccount();
            while (gsid == null) {
                // 队列中的所有账号当前均不可用，停顿5分钟再试
                Thread.sleep(5 * 60 * 1000);
                gsid = switchAccount();
            }
        }

        return gsid;
    }

    protected CookieStore process(String result, CookieStore cookie) throws InterruptedException {
        // 处理成功，未被冻结账号，停顿200ms，防止频繁访问被快速冻结账号
        if (result.equals(Constants.OK)) {
            Thread.sleep(200);
        }
        // 系统繁忙
        else if (result.equals(Constants.SYSTEM_BUSY)) {
            logger.info("server busy, retry after 5s...");
            Thread.sleep(5 * 1000);
        }
        // 账号被冻结，切换账号继续执行
        else if (result.equals(Constants.ACCOUNT_FORBIDDEN)) {
            logger.info("Account has been frozen: {}", username);
            // 切换账号
            cookie = switchAccountForCookie();
            while (cookie == null) {
                // 队列中的所有账号当前均不可用，停顿5分钟，再试
                Thread.sleep(5 * 60 * 1000);
                cookie = switchAccountForCookie();
            }
        }

        return cookie;
    }

    /**
     * 根据用户名和密码登录微博手机版，并返回维护会话的gsid
     * 登录失败时返回null
     *
     * @param username
     * @param password
     * @return
     */
    protected String login(String username, String password) {
        return (new LoginWorker()).loginAndGetGsid(username, password);
    }

    /**
     * 根据用户名和密码登录微博手机版，并返回维护会话的cookie
     * 登录失败时返回null
     *
     * @param username
     * @param password
     * @return
     */
    protected CookieStore loginForCookie(String username, String password) {
        return (new LoginWorker()).loginAndGetCookie(username, password);
    }

    /**
     * 切换账户并登录
     *
     * @return
     * @throws IOException
     */
    protected String switchAccount() {
        Account account;
        String gsid;
        do {
            // 从队列头取出一个账户，并将其添加到队尾等待下一次使用
            account = AccountQueue.outElement();
            AccountQueue.addElement(account);
            // 使用切换账号登录
            gsid = login(account.getUserName(), account.getPassword());
            // 登录成功，退出循环
            if (gsid != null) {
                this.username = account.getUserName();
                this.password = account.getPassword();
                String logStr = "Switch to account: " + account.getUserName() + " success!";
                Utils.writeLog(LogType.SWITCH_ACCOUNT_LOG, logStr);
                break;
            }
            String logStr = "Switch to account: " + account.getUserName() + " failed!";
            Utils.writeLog(LogType.SWITCH_ACCOUNT_LOG, logStr);
        }
        // 如果取出的账号与当前的账号相同，则退出，表明队列中所有的账号都被试用一圈，均不可用
        while (!account.getUserName().equals(username));

        return gsid;
    }

    /**
     * 切换账户并登录
     *
     * @return
     * @throws IOException
     */
    protected CookieStore switchAccountForCookie() {
        Account account;
        CookieStore cookie;
        do {
            // 从队列头取出一个账户，并将其添加到队尾等待下一次使用
            account = AccountQueue.outElement();
            AccountQueue.addElement(account);
            // 使用切换账号登录
            cookie = loginForCookie(account.getUserName(), account.getPassword());
            // 登录成功，退出循环
            if (cookie != null) {
                this.username = account.getUserName();
                this.password = account.getPassword();
                String logStr = "Switch to account: " + account.getUserName() + " success!";
                Utils.writeLog(LogType.SWITCH_ACCOUNT_LOG, logStr);
                break;
            }
            String logStr = "Switch to account: " + account.getUserName() + " failed!";
            Utils.writeLog(LogType.SWITCH_ACCOUNT_LOG, logStr);
        }
        // 如果取出的账号与当前的账号相同，则退出，表明队列中所有的账号都被试用一圈，均不可用
        while (!account.getUserName().equals(username));

        return cookie;
    }

}
