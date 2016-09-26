package com.alibaba.aliyun.crazyacking.spider.parser;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by crazyacking on 2016/7/1.
 */
class WebDataExtraction {
    private static final Logger logger = LoggerFactory.getLogger(WebDataExtraction.class);

    public void BeginWebDataExtraction(String contentString) throws InterruptedException {
        Thread.sleep(500);

        String regCharSet = "[\\u4e00-\\u9fa5]+.*";
        Pattern pattern = Pattern.compile(regCharSet);
        Matcher matcher = pattern.matcher(contentString);
        String uid;
        String userSex = null;
        String nickName = null;
        String realName = null;
        String userBlogs = null;
        String creditScore = null;
        String userLocation = null;
        String userBirthday = null;
        String creditRating = null;
        String empiricalValue = null;
        String microBlogLevel = null;
        String userIntroduction = null;
        String registrationTime = null;

        int uidSta = contentString.indexOf("oid");
        int uidStaLen = 0;
        for (int i = uidSta + 7; contentString.charAt(i) != '\''; ++i) {
            uidStaLen++;
        }
        uid = contentString.substring(uidSta + 7, uidSta + 7 + uidStaLen);

        int len1 = 0, len2 = 0, len3 = 0, len4 = 0, len5 = 0, len6 = 0, len7 = 0, len8 = 0, len9 = 0, len10 = 0, len11 = 0, len12 = 0;
        while (matcher.find()) {
            String groupString = matcher.group();

            if (groupString.contains("昵称：")) {
                int subStringStartPos = groupString.indexOf("昵称：") + 49;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len1++;
                }
                nickName = groupString.substring(subStringStartPos, subStringStartPos + len1);
            }


            if (groupString.contains("真实姓名：")) {
                int subStringStartPos = groupString.indexOf("真实姓名：") + 39;
                for (int i = subStringStartPos; groupString.charAt(i) != '<'; ++i) {
                    len2++;
                }
                realName = groupString.substring(subStringStartPos, subStringStartPos + len2);
            }

            if (groupString.contains("所在地：")) {
                int subStringStartPos = groupString.indexOf("所在地：") + 39 + 11;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len3++;
                }
                userLocation = groupString.substring(subStringStartPos, subStringStartPos + len3);
            }


            if (groupString.contains("性别：")) {
                int subStringStartPos = groupString.indexOf("性别：") + 49;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len4++;
                }
                userSex = groupString.substring(subStringStartPos, subStringStartPos + len4);
            }


            if (groupString.contains("生日：")) {
                int subStringStartPos = groupString.indexOf("生日：") + 49;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len5++;
                }
                userBirthday = groupString.substring(subStringStartPos, subStringStartPos + len5);
            }


            if (groupString.contains("博客：")) {
                int subStringStartPos = groupString.indexOf("博客：") + 75;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len6++;
                }
                userBlogs = groupString.substring(subStringStartPos, subStringStartPos + len6);
            }


            if (groupString.contains("简介：")) {
                int subStringStartPos = groupString.indexOf("简介：") + 89;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len7++;
                }
                userIntroduction = groupString.substring(subStringStartPos, subStringStartPos + len7);
            }


            if (groupString.contains("注册时间：")) {
                int subStringStartPos = groupString.indexOf("注册时间：") + 115;
                for (int i = subStringStartPos; groupString.charAt(i) != '\\'; ++i) {
                    len8++;
                }
                registrationTime = groupString.substring(subStringStartPos, subStringStartPos + len8);
            }


            if (groupString.contains("当前等级：")) {
                int subStringStartPos = groupString.indexOf("当前等级：") + 39;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len9++;
                }
                microBlogLevel = groupString.substring(subStringStartPos, subStringStartPos + len9);
            }


            if (groupString.contains("当前信用积分：")) {
                int subStringStartPos = groupString.indexOf("当前信用积分：") + 42;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len10++;
                }
                creditScore = groupString.substring(subStringStartPos, subStringStartPos + len10);
            }


            if (groupString.contains("信用等级：")) {
                int subStringStartPos = groupString.indexOf("信用等级：") + 34;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len11++;
                }
                creditRating = groupString.substring(subStringStartPos, subStringStartPos + len11);
            }


            if (groupString.contains("经验值：")) {
                int subStringStartPos = groupString.indexOf("经验值：") + 35;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len12++;
                }
                empiricalValue = groupString.substring(subStringStartPos, subStringStartPos + len12);
            }
        }

        String connectionUrl = "jdbc:sqlserver://localhost:1433;" + "databaseName=sina_weibo;integratedSecurity=true;";
        String url = "jdbc:sqlserver://127.0.0.1:1433;databaseName=sina_weibo;user=crazyacking;password=jiangshanbiao.";//sa身份连接
        String url2 = "jdbc:sqlserver://127.0.0.1:1433;databaseName=sina_weibo;integratedSecurity=true;";//windows集成模式连接
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // Establish the connection.
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(url);
            Thread.sleep(500);

            // Create and execute an SQL statement that returns some data.
            String SQL = "INSERT INTO USER_INFO_BASE_TABLE\n"
                    + "VALUES(" + "\'" + nickName + "\'" + ',' + "\'" + uid
                    + "\'" + ',' + "\'" + realName + "\'" + ',' + "\'" + userLocation
                    + "\'" + ',' + "\'" + userSex + "\'" + ',' + "\'" + userBirthday + "\'" + ',' + "\'"
                    + userBlogs + "\'" + ',' + "\'" + userIntroduction + "\'" + ',' + "\'" + registrationTime
                    + "\'" + ',' + "\'" + microBlogLevel + "\'" + ',' + "\'" + creditScore + "\'" + ',' + "\'"
                    + creditRating + "\'" + ',' + "\'" + empiricalValue + "\'" + ")";
            stmt = con.createStatement();
            int Result = stmt.executeUpdate(SQL);

        } catch (Exception e) {
            logger.error(e.toString());
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception ignored) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Exception ignored) {
                }
            if (con != null)
                try {
                    con.close();
                } catch (Exception ignored) {
                }
        }
    }


}
