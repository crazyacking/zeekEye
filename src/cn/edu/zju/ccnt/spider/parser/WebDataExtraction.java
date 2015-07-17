package cn.edu.zju.ccnt.spider.parser;


import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Connection;

import static java.lang.Integer.*;

/**
 * Created by crazyacking on 2015/7/1.
 */
public class WebDataExtraction {

    public void BeginWebDataExtraction(String contentString) throws InterruptedException {
        System.out.println("正在从网页提取数据文件...");
        Thread.sleep(500);
        String regCharSet = "[\\u4e00-\\u9fa5]+.*";
        Pattern p = Pattern.compile(regCharSet);
        Matcher m = p.matcher(contentString);
        String nickName = "Unknown";
        String realName = "Unknown";
        String userLocation = "Unknown";
        String userSex = "Unknown";
        String userBirthday = "Unknown";
        String userBlogs = "Unknown";
        String userBriefIntroduction = "Unknown";
        String RegistrationTime = "Unknown";
        String MicroBlogLevel = "Unknown";
        String CreditScore = "Unknown";
        String CreditRating = "Unknown";
        String EmpiricalValue = "Unknown";
        String uid = "Unknown";
        int uidSta = contentString.indexOf("oid");
        int uidStaLen = 0;
        for (int i = uidSta + 7; contentString.charAt(i) != '\''; ++i) {
            uidStaLen++;
        }
        uid = contentString.substring(uidSta + 7, uidSta + 7 + uidStaLen);
        System.out.println("uid = " + uid);

        int len1 = 0, len2 = 0, len3 = 0, len4 = 0, len5 = 0, len6 = 0, len7 = 0, len8 = 0, len9 = 0, len10 = 0, len11 = 0, len12 = 0;
        while (m.find()) {
            String groupString = m.group();

            if (groupString.indexOf("昵称：") != -1) {
                int subStringStartPos = groupString.indexOf("昵称：") + 49;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len1++;
                }
                nickName = groupString.substring(subStringStartPos, subStringStartPos + len1);
            }


            if (groupString.indexOf("真实姓名：") != -1) {
                int subStringStartPos = groupString.indexOf("真实姓名：") + 39;
                for (int i = subStringStartPos; groupString.charAt(i) != '<'; ++i) {
                    len2++;
                }
                realName = groupString.substring(subStringStartPos, subStringStartPos + len2);
            }

            if (groupString.indexOf("所在地：") != -1) {
                int subStringStartPos = groupString.indexOf("所在地：") + 39 + 11;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len3++;
                }
                userLocation = groupString.substring(subStringStartPos, subStringStartPos + len3);
            }


            if (groupString.indexOf("性别：") != -1) {
                int subStringStartPos = groupString.indexOf("性别：") + 49;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len4++;
                }
                userSex = groupString.substring(subStringStartPos, subStringStartPos + len4);
            }


            if (groupString.indexOf("生日：") != -1) {
                int subStringStartPos = groupString.indexOf("生日：") + 49;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len5++;
                }
                userBirthday = groupString.substring(subStringStartPos, subStringStartPos + len5);
            }


            if (groupString.indexOf("博客：") != -1) {
                int subStringStartPos = groupString.indexOf("博客：") + 75;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len6++;
                }
                userBlogs = groupString.substring(subStringStartPos, subStringStartPos + len6);
            }


            if (groupString.indexOf("简介：") != -1) {
                int subStringStartPos = groupString.indexOf("简介：") + 89;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len7++;
                }
                userBriefIntroduction = groupString.substring(subStringStartPos, subStringStartPos + len7);
            }


            if (groupString.indexOf("注册时间：") != -1) {
                int subStringStartPos = groupString.indexOf("注册时间：") + 115;
                for (int i = subStringStartPos; groupString.charAt(i) != '\\'; ++i) {
                    len8++;
                }
                RegistrationTime = groupString.substring(subStringStartPos, subStringStartPos + len8);
            }


            if (groupString.indexOf("当前等级：") != -1) {
                int subStringStartPos = groupString.indexOf("当前等级：") + 39;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len9++;
                }
                MicroBlogLevel = groupString.substring(subStringStartPos, subStringStartPos + len9);
            }


            if (groupString.indexOf("当前信用积分：") != -1) {
                int subStringStartPos = groupString.indexOf("当前信用积分：") + 42;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len10++;
                }
                CreditScore = groupString.substring(subStringStartPos, subStringStartPos + len10);
            }


            if (groupString.indexOf("信用等级：") != -1) {
                int subStringStartPos = groupString.indexOf("信用等级：") + 34;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len11++;
                }
                CreditRating = groupString.substring(subStringStartPos, subStringStartPos + len11);
            }


            if (groupString.indexOf("经验值：") != -1) {
                int subStringStartPos = groupString.indexOf("经验值：") + 35;
                for (int i = subStringStartPos; groupString.charAt(i) != '&'; ++i) {
                    len12++;
                }
                EmpiricalValue = groupString.substring(subStringStartPos, subStringStartPos + len12);
            }
        }
        System.out.println("=====================================================================");
        System.out.println("昵称：" + nickName);
        System.out.println("微博ID：" + uid);
        System.out.println("真实姓名：" + realName);
        System.out.println("所在地：" + userLocation);
        System.out.println("性别：" + userSex);
        System.out.println("生日：" + userBirthday);
        System.out.println("博客：" + userBlogs);
        System.out.println("简介：" + userBriefIntroduction);
        System.out.println("注册时间：" + RegistrationTime);
        System.out.println("微博等级：" + MicroBlogLevel);
        System.out.println("信用积分：" + CreditScore);
        System.out.println("信用等级：" + CreditRating);
        System.out.println("经验值：" + EmpiricalValue);

        System.out.println("正在导入数据到数据库...");
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
            System.out.println("成功导入数据库！");

            // Create and execute an SQL statement that returns some data.


            System.out.println("uid= " + uid);
            String SQL = "INSERT INTO USER_INFO_BASE_TABLE\n" + "VALUES(" + "\'" + nickName + "\'" + ',' + "\'" + uid + "\'" + ',' + "\'" + realName + "\'" + ',' + "\'" + userLocation + "\'" + ',' + "\'" + userSex + "\'" + ',' + "\'" + userBirthday + "\'" + ',' + "\'" + userBlogs + "\'" + ',' + "\'" + userBriefIntroduction + "\'" + ',' + "\'" + RegistrationTime + "\'" + ',' + "\'" + MicroBlogLevel + "\'" + ',' + "\'" + CreditScore + "\'" + ',' + "\'" + CreditRating + "\'" + ',' + "\'" + EmpiricalValue + "\'" + ")";
            System.out.println(SQL);
            System.out.println("SQL语句成功执行！");
            stmt = con.createStatement();
            int Result = stmt.executeUpdate(SQL);

//             Iterate through the data in the result set and display it.
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Exception e) {
                }
            if (con != null)
                try {
                    con.close();
                } catch (Exception e) {
                }
        }
    }


}
