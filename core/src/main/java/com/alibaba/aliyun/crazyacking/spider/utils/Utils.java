package com.alibaba.aliyun.crazyacking.spider.utils;

import com.alibaba.aliyun.crazyacking.spider.fetcher.FetcherType;
import com.alibaba.aliyun.crazyacking.spider.parser.LogType;
import com.alibaba.aliyun.crazyacking.spider.parser.bean.Account;
import com.alibaba.aliyun.crazyacking.spider.queue.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static final Connection conn = DBConnector.getConnection();
    private static final Logger logger = LoggerFactory.getLogger(Utils.class.getName());
    private static final SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 检测字符串是否为null，或空字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmptyStr(String str) {
        return str == null || str.trim().length() == 0;

    }

    /**
     * 将微博时间字符串转换成yyyyMMddHHmmSS
     * 微博时间字符串有：
     * xx分钟前
     * 今天 11:53
     * 07月09日 13:36
     * 2010-09-23 19:55:38
     *
     * @param weiboTimeStr
     * @return
     */
    public static String parseDate(String weiboTimeStr) {

        Calendar currentTime = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。

        if (weiboTimeStr.contains("分钟前")) {
            int minutes = Integer.parseInt(weiboTimeStr.split("分钟前")[0]);

            currentTime.add(Calendar.MINUTE, -minutes);//取当前日期的前一天.

            return simpleDateTimeFormat.format(currentTime.getTime());
        } else if (weiboTimeStr.startsWith("今天")) {
            String[] time = weiboTimeStr.split("天")[1].split(":");
            int hour = Integer.parseInt(time[0].substring(1));
            int minute = Integer.parseInt(time[1].substring(0, 2));

            currentTime.set(Calendar.HOUR_OF_DAY, hour);
            currentTime.set(Calendar.MINUTE, minute);

            return simpleDateTimeFormat.format(currentTime.getTime());
        } else if (weiboTimeStr.contains("月")) {
            String[] time = weiboTimeStr.split("日")[1].split(":");
            int dayIndex = weiboTimeStr.indexOf("日") - 2;
            int month = Integer.parseInt(weiboTimeStr.substring(0, 2));
            int day = Integer.parseInt(weiboTimeStr.substring(dayIndex, dayIndex + 2));
            int hour = Integer.parseInt(time[0].substring(1));
            int minute = Integer.parseInt(time[1].substring(0, 2));

            currentTime.set(Calendar.MONTH, month - 1);
            currentTime.set(Calendar.DAY_OF_MONTH, day);
            currentTime.set(Calendar.HOUR_OF_DAY, hour);
            currentTime.set(Calendar.MINUTE, minute);

            return simpleDateTimeFormat.format(currentTime.getTime());
        } else if (weiboTimeStr.contains("-")) {
            return weiboTimeStr.replace("-", "").replace(":", "").replace(" ", "").substring(0, 14);
        } else {
            logger.info(">> Error: Unknown time format - " + weiboTimeStr);
        }

        return null;
    }

    /**
     * 根据logType将日志写入相应的文件
     *
     * @param logType
     * @param logStr
     */
    public static void writeLog(int logType, String logStr) {
        // 选取log类型
        String filePath;
        switch (logType) {
            case LogType.SWITCH_ACCOUNT_LOG:
                filePath = Constants.SWITCH_ACCOUNT_LOG_PATH;
                break;
            case LogType.COMMENT_LOG:
                filePath = Constants.COMMENT_LOG_PATH;
                break;
            case LogType.REPOST_LOG:
                filePath = Constants.REPOST_LOG_PATH;
                break;
            case LogType.WEIBO_LOG:
                filePath = Constants.ABNORMAL_WEIBO_PATH;
                break;
            default:
                return;
        }

        // 写入日志
        try {
            FileWriter fileWriter = new FileWriter(filePath, true);
            if (logType == LogType.WEIBO_LOG) {
                fileWriter.write(logStr + "\r\n");
            } else {
                fileWriter.write((new Date()).toString() + ": " + logStr + "\r\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    /**
     * 将异常账号写入文件
     *
     * @param account
     * @throws IOException
     */
    private static void writeAbnormalAccount(String account) throws IOException {
        FileWriter fileWriter = new FileWriter(Constants.ABNORMAL_ACCOUNT_PATH, true);
        fileWriter.write(account + "\r\n");
        fileWriter.flush();
        fileWriter.close();
    }

    // 从url中解析出当前用户的ID
    public static String getUserIdFromUrl(String url) {
        int startIndex = url.lastIndexOf("/");
        int endIndex = url.indexOf("?");

        if (endIndex == -1) {
            return url.substring(startIndex + 1);
        }
        return url.substring(startIndex + 1, endIndex);
    }

    // 从follow url中解析出当前用户的ID
    public static String getUserIdFromFollowUrl(String url) {
        int startIndex = 16;
        int endIndex = url.indexOf("/follow");

        return url.substring(startIndex, endIndex);
    }

    // http://tp2.sinaimg.cn/2826608265/50/5667697175/1
    public static String getUserIdFromImgUrl(String url) {
        int startIndex = url.indexOf("sinaimg.cn/") + "sinaimg.cn/".length();
        String subStr = url.substring(startIndex);

        return subStr.substring(0, subStr.indexOf("/"));
    }

    /**
     * 从login_account.txt中读取爬虫账号，作为账号队列
     * 格式：account----email----password
     */
    public static void readAccountFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Constants.LOGIN_ACCOUNT_PATH));
            String accountLine;
//			logger.info("program running here..");
            while (((accountLine = reader.readLine()) != null)) {
                String[] account = accountLine.split("----");
//				logger.info(account[0]);
//				logger.info(account[2]);
                AccountQueue.addElement(new Account(account[0], account[2]));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            logger.error(e.toString());
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }


    public static synchronized void addNextLevelFollower(int currentLevel) {

        String querySql = "SELECT DISTINCT followee FROM follow WHERE LEVEL = ? AND followee NOT IN(SELECT DISTINCT follower FROM follow )";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = conn.prepareStatement(querySql);
            ps.setInt(1, currentLevel);
            rs = ps.executeQuery();
            while (rs.next()) {
                FollowUrlQueue.addElement("http://weibo.cn/" + rs.getString("followee") + "/follow");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error(e.toString());
        }
    }

    public static String checkContent(String content, String url, int fetcherType) throws IOException {
        String returnMsg = null;
        // 检测当前访问的用户是否为异常账号
        if (content.contains("用户不存在哦!")
                || content.contains("请求页不存在")
                || content.contains("您当前访问的用户状态异常，暂时无法访问")) {

            AbnormalAccountUrlQueue.addElement(url);

            logger.info(">> 当前访问的用户是异常账号: " + content);
            logger.info("-----------------------------------");

            if (fetcherType == FetcherType.COMMENT) {
                Utils.writeAbnormalAccount(Utils.getUserIdFromUrl(url));
                logger.info("抓取到的连接数：" + CommentUrlQueue.size());
                logger.info("已处理的页面数：" + VisitedCommentUrlQueue.size());
            } else if (fetcherType == FetcherType.REPOST) {
                Utils.writeAbnormalAccount(Utils.getUserIdFromUrl(url));
                logger.info("抓取到的连接数：" + RepostUrlQueue.size());
                logger.info("已处理的页面数：" + VisitedRepostUrlQueue.size());
            } else if (fetcherType == FetcherType.WEIBO) {
                Utils.writeAbnormalAccount(Utils.getUserIdFromUrl(url));
                logger.info("抓取到的连接数：" + WeiboUrlQueue.size());
                logger.info("已处理的页面数：" + VisitedWeiboUrlQueue.size());
            } else if (fetcherType == FetcherType.FOLLOW) {
                Utils.writeAbnormalAccount(Utils.getUserIdFromFollowUrl(url));
                logger.info("抓取到的连接数：" + FollowUrlQueue.size());
                logger.info("已处理的页面数：" + VisitedFollowUrlQueue.size());
            }

            logger.info("异常账号数         ：" + AbnormalAccountUrlQueue.size());
            logger.info("----------------------------------");
            returnMsg = Constants.OK;
        }
        // 检测账号是否被冻结
        else if (content.contains(Constants.FORBIDDEN_PAGE_TITILE)
                || content.contains("<div class=\"c\">你的微博账号出现异常被暂时冻结!<br/>完成以下操作即可激活你的微博。<br/></div>")
                || content.contains("<div class=\"c\">抱歉，你的帐号存在异常，暂时无法访问。<br/>")
                || content.contains("<div class=\"c\">您的帐号存在异常，暂时无法访问。<br/>")
                || content.contains("<div class=\"c\">您的微博帐号出现异常被暂时冻结。<br/>")
                || content.contains("<div class=\"c\">完成验证后即可开始微博之旅：</div>")) {
            // 被暂时冻结账号了，当前url没有处理，移除原gsid，返回队列头部，并返回forbidden
            url = url.split("&gsid")[0];
            logger.info(">> Put back url: " + url);
            logger.info(">> 当前账号被冻结!");

            if (fetcherType == FetcherType.COMMENT) {
                CommentUrlQueue.addFirstElement(url);
            } else if (fetcherType == FetcherType.REPOST) {
                RepostUrlQueue.addFirstElement(url);
            } else if (fetcherType == FetcherType.WEIBO) {
                WeiboUrlQueue.addFirstElement(url);
            } else if (fetcherType == FetcherType.FOLLOW) {
                FollowUrlQueue.addFirstElement(url);
            }

            returnMsg = Constants.ACCOUNT_FORBIDDEN;
        }
        // 监测系统繁忙错误
        else if (content.contains("<div class=\"me\">系统繁忙,请稍后再试!</div>")) {
            // 系统繁忙，当前url没有处理，移除原gsid，返回队列头部，并返回busy
            url = url.split("&gsid")[0];
            logger.info(">> Put back url: " + url);
            logger.info(">> 系统繁忙: " + content);

            if (fetcherType == FetcherType.COMMENT) {
                CommentUrlQueue.addFirstElement(url);
            } else if (fetcherType == FetcherType.REPOST) {
                RepostUrlQueue.addFirstElement(url);
            } else if (fetcherType == FetcherType.WEIBO) {
                WeiboUrlQueue.addFirstElement(url);
            }

            returnMsg = Constants.SYSTEM_BUSY;
        }

        return returnMsg;
    }

    public static void handleAbnormalWeibo(String content, String url) {
        String[] urlParts = url.split("page=");
        int page = Integer.parseInt(urlParts[1]);
        int weiboNum = Integer.parseInt(content.split("<div class=\"tip2\"><span class=\"tc\">微博\\[")[1].split("\\]")[0]);

        if (page * 10 < weiboNum) {
            Utils.writeLog(LogType.WEIBO_LOG, url);
            String nextUrl = urlParts[0] + (page + 1);
            WeiboUrlQueue.addElement(nextUrl);
        }

    }
}
