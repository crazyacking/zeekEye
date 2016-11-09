package com.alibaba.aliyun.crazyacking.spider.parser;

import com.alibaba.aliyun.crazyacking.spider.common.Constants;
import com.alibaba.aliyun.crazyacking.spider.common.DBConnector;
import com.alibaba.aliyun.crazyacking.spider.common.Utils;
import com.alibaba.aliyun.crazyacking.spider.parser.bean.Follow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class FollowParser {
    public static final Connection conn = DBConnector.getConnection();
    private static final Logger logger = LoggerFactory.getLogger(FollowParser.class.getName());

    public static Document getPageDocument(String content) {
        return Jsoup.parse(content);
    }

    // 截取网页网页源文件的目标内容
    public static List<Element> getGoalContent(Document doc) {

        return doc.getElementsByTag("table");
    }

    // 解析每一条followee的结构，创建Follow对象
    private static Follow parse(Element followEl, String followerID, int currentLevel) {
        Follow follow = new Follow();
        try {
            int fansOfFollowee = 0;
            for (TextNode text : followEl.getElementsByTag("td").get(1).textNodes()) {
                if (text.toString().startsWith("粉丝")) {
                    int startIndex = "粉丝".length();
                    int endIndex = text.toString().indexOf("人");
                    fansOfFollowee = Integer.parseInt(text.toString().substring(startIndex, endIndex));
                }
            }

            // 根据fansOfFollowee进行过滤

            String followeeUrl = followEl.getElementsByTag("img").get(0).attr("src");
            String followeeID = Utils.getUserIdFromImgUrl(followeeUrl);

            follow.setFollower(followerID);
            follow.setFollowed(followeeID);
            follow.setLevel(currentLevel);
        } catch (Exception e) {
            follow = null;
            logger.error("Not a valid follow item: " + followEl, e);
        }

        return follow;
    }

    // 将抓取的微博信息保存至本地文件
    public static void createFile(List<Element> followeeItems, String urlPath, int currentLevel) {

        String followerID = Utils.getUserIdFromFollowUrl(urlPath);
        int level = currentLevel + 1;

        // 解析每一条followee，提取各部分内容，并写入数据库
        PreparedStatement ps;
        for (Element followeeItem : followeeItems) {
            try {
                // 写入follow表，新的关注关系
                ps = conn.prepareStatement("INSERT INTO follow (follower, followee, level) VALUES (?, ?, ?)");

                Follow follow = FollowParser.parse(followeeItem, followerID, level);

                if (follow != null) {
                    ps.setString(1, follow.getFollower());
                    ps.setString(2, follow.getFollowed());
                    ps.setInt(3, follow.getLevel());
                    ps.execute();
                }
                ps.close();


                // 在未达到最后一轮的情况下，写入follower表，下一轮抓取的ID
                if (follow != null && level < Constants.LEVEL) {
                    ps = conn.prepareStatement("INSERT INTO follower (follower, level) VALUES (?, ?)");

                    ps.setString(1, follow.getFollowed());
                    ps.setInt(2, level);
                    ps.execute();
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("", e);
            }
        }
    }

}
