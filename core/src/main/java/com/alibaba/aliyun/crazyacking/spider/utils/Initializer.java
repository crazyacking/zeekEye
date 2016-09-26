package com.alibaba.aliyun.crazyacking.spider.utils;

import com.alibaba.aliyun.crazyacking.spider.queue.CommentUrlQueue;
import com.alibaba.aliyun.crazyacking.spider.queue.FollowUrlQueue;
import com.alibaba.aliyun.crazyacking.spider.queue.RepostUrlQueue;
import com.alibaba.aliyun.crazyacking.spider.queue.WeiboUrlQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * Created by crazyacking on 2016/7/3.
 */
public class Initializer {

    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);
    private static final Connection conn = DBConnector.getConnection();

    /**
     * 数据库中读取用户账号，并生成第一页微博的url，放入WeiboUrlQueue
     */
    public static synchronized void initializeWeiboUrl() {
        String querySql = "SELECT accountID FROM INIT_USER WHERE isFetched = 0";
        Connection conn = DBConnector.getConnection();
        PreparedStatement ps;
        Statement st;
        ResultSet rs;
        String accountID = null;

        try {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            st = conn.createStatement();
            rs = st.executeQuery(querySql);
            if (rs.next()) {
                accountID = rs.getString("accountID");
                ps = conn.prepareStatement("UPDATE INIT_USER SET isFetched = 1 WHERE accountID = ?");
                ps.setString(1, accountID);
                ps.execute();
                ps.close();
            }
            rs.close();
            st.close();

            conn.commit();
            if (accountID != null) {
                // 提交成功后，再放入队列
                /*
                * 将初始用户的粉丝页面的Url入队
				* */
                logger.info("accountID=" + accountID);
                WeiboUrlQueue.addElement("http://weibo.com/p/100505" + accountID + "/follow?relate=fans&from=100505&wvr=6&mod=headfans&current=fans#place");
                logger.info("the url is =   " + "http://weibo.com/p/100505" + accountID + "/follow?relate=fans&from=100505&wvr=6&mod=headfans&current=fans#place");
            }
        } catch (SQLException e) {
            logger.error(e.toString());
            // 提交失败 roll back，并将放入队列的URL拿出来
            try {
                conn.rollback();
            } catch (SQLException e1) {
                logger.error(e1.toString());
            }
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.toString());
            }
        }
    }

    /**
     * 数据库中读取用微博账号，并生成第一页评论的url，放入CommentUrlQueue
     */
    public static synchronized void initializeCommentUrl() {
        String querySql = "SELECT weiboID FROM weibo WHERE isCommentFetched = 0 LIMIT 1";
//		Connection conn = DBConnector.getConnection();
        PreparedStatement ps;
        Statement st;
        ResultSet rs;
        String weiboID = null;

        try {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            st = conn.createStatement();
            rs = st.executeQuery(querySql);
            if (rs.next()) {
                weiboID = rs.getString("weiboID");
                ps = conn.prepareStatement("UPDATE weibo SET isCommentFetched = 1 WHERE weiboID = ?");
                ps.setString(1, weiboID);
                ps.execute();
                ps.close();
            }
            rs.close();
            st.close();

            conn.commit();
            if (weiboID != null) {
                // 提交成功后，再放入队列
                CommentUrlQueue.addElement(Constants.COMMENT_BASE_STR + weiboID + "?page=1");
            }
        } catch (SQLException e) {
            logger.error(e.toString());
            // 提交失败 roll back，并将放入队列的URL拿出来
            try {
                conn.rollback();
            } catch (SQLException e1) {
                logger.error(e1.toString());
            }
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.toString());
            }
        }
    }

    /**
     * 数据库中读取微博账号，并生成第一页转发的url，放入WeiboUrlQueue
     */
    public static synchronized void initializeRepostUrl() {
        String querySql = "SELECT weiboID FROM weibo WHERE isRepostFetched = 0 LIMIT 1";
//		Connection conn = DBConnector.getConnection();
        PreparedStatement ps;
        Statement st;
        ResultSet rs;
        String weiboID = null;

        try {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            st = conn.createStatement();
            rs = st.executeQuery(querySql);
            if (rs.next()) {
                weiboID = rs.getString("weiboID");
                ps = conn.prepareStatement("UPDATE weibo SET isRepostFetched = 1 WHERE weiboID = ?");
                ps.setString(1, weiboID);
                ps.execute();
                ps.close();
            }
            rs.close();
            st.close();

            conn.commit();
            if (weiboID != null) {
                // 提交成功后，再放入队列
                RepostUrlQueue.addElement(Constants.REPOST_BASE_STR + weiboID + "?page=1");
            }
        } catch (SQLException e) {
            logger.error(e.toString());
            // 提交失败 roll back，并将放入队列的URL拿出来
            try {
                conn.rollback();
            } catch (SQLException e1) {
                logger.error(e1.toString());
            }
        }
    }

    /**
     * 从account.txt中读取用户账号，并生成用户主页的url，放入AccountInfoUrlQueue
     */
    public static void initializeAbnormalWeiboUrl() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.ABNORMAL_WEIBO_CLEANED_PATH), "utf-8"));

            String accountLine;
            while ((accountLine = reader.readLine()) != null) {
                WeiboUrlQueue.addElement(accountLine);
            }
            reader.close();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public static int initializeFollowUrl() {
//		String querySql = "SELECT follower, level FROM follower WHERE isFetched = 0 LIMIT 1";
        String querySql = "SELECT follower, LEVEL FROM follower WHERE isFetched = 0 ORDER BY LEVEL ASC LIMIT 1";
        PreparedStatement ps;
        Statement st;
        ResultSet rs;
        String followerID = null;
        int level = Integer.MAX_VALUE;

        try {
            // 获取本轮follower，level
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            st = conn.createStatement();
            rs = st.executeQuery(querySql);
            if (rs.next()) {
                followerID = rs.getString("follower");
                level = rs.getInt("level");
                ps = conn.prepareStatement("UPDATE follower SET isFetched = 1 WHERE follower = ?");
                ps.setString(1, followerID);
                ps.execute();
                ps.close();
            }
            rs.close();
            st.close();

            conn.commit();

            // 当本轮level < Constants.LEVEL，才添加队列URL
            if (level < Constants.LEVEL) {
                FollowUrlQueue.addElement("http://weibo.cn/" + followerID + "/follow");
            }
        } catch (SQLException e) {
            logger.error(e.toString());

            // 提交失败 roll back
            try {
                conn.rollback();
            } catch (SQLException e1) {
                logger.error(e1.toString());
            }
        }

        return level;
    }

    /**
     * 从配置文件中读取配置信息：数据库连接、相关文件根目录、爬虫任务类型
     */
    public void initializeParams() {
        InputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream("conf\\spider.properties"));
            Properties properties = new Properties();
            properties.load(in);

            // 从配置文件中读取数据库连接参数
            DBConnector.CONN_URL = properties.getProperty("DB.connUrl");
            DBConnector.DB_NAME = properties.getProperty("DB.name");
            DBConnector.USERNAME = properties.getProperty("DB.username");
            DBConnector.PASSWORD = properties.getProperty("DB.password");

            // 从配置文件中读取根目录，并设置相关文件地址
            Constants.ROOT_DISK = properties.getProperty("spider.rootDisk");
            Constants.REPOST_LOG_PATH = Constants.ROOT_DISK + "repost_log.txt";
            Constants.COMMENT_LOG_PATH = Constants.ROOT_DISK + "comment_log.txt";
            Constants.SWITCH_ACCOUNT_LOG_PATH = Constants.ROOT_DISK + "switch_account_log.txt";
            Constants.ACCOUNT_PATH = Constants.ROOT_DISK + "account.txt";
            Constants.ACCOUNT_RESULT_PATH = Constants.ROOT_DISK + "account_result.txt";
            Constants.LOGIN_ACCOUNT_PATH = Constants.ROOT_DISK + "login_account.txt";
            Constants.ABNORMAL_ACCOUNT_PATH = Constants.ROOT_DISK + "abnormal_account.txt";
            Constants.ABNORMAL_WEIBO_PATH = Constants.ROOT_DISK + "abnormal_weibo.txt";
            Constants.ABNORMAL_WEIBO_CLEANED_PATH = Constants.ROOT_DISK + "abnormal_weibo_cleaned.txt";

            // 从配置文件中读取爬虫任务类型
//            WeiboSpiderStarter.TYPE = properties.getProperty("spider.type");

            // 从配置文件中读取follow爬取相关参数
//            if (TYPE.equals("follow")) {
//                Constants.LEVEL = Integer.parseInt(properties.getProperty("follow.level"));
//                Constants.FANS_NO_MORE_THAN = Integer.parseInt(properties.getProperty("follow.maxFansNum"));
//            }

            // 从配置文件中读取微博相关参数
            Constants.CHECK_WEIBO_NUM = Boolean.parseBoolean(properties.getProperty("weibo.checkWeiboNum", "false"));
            if (Constants.CHECK_WEIBO_NUM) {
                Constants.WEIBO_NO_MORE_THAN = Integer.parseInt(properties.getProperty("weibo.maxWeiboNum"));
            }

            in.close();
        } catch (FileNotFoundException e) {
            logger.error(e.toString());
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }
}
