package cn.edu.hut.crazyacking.spider.parser;

import cn.edu.hut.crazyacking.spider.common.Constants;
import cn.edu.hut.crazyacking.spider.common.DBConnector;
import cn.edu.hut.crazyacking.spider.common.Utils;
import cn.edu.hut.crazyacking.spider.parser.bean.Weibo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WeiboParser {
    public static final Connection conn = DBConnector.getConnection();
    private static final int BACK_NODES_NUM_IN_REPOST_DIV = 9;
    private static final Logger logger = LoggerFactory.getLogger(WeiboParser.class.getName());

    public static Document getPageDocument(String content) {
        return Jsoup.parse(content);
    }

    /*
    截取网页网页源文件的目标内容
     */
    public static List<Element> getGoalContent(Document doc) {
        List<Element> weiboItems = new ArrayList<>();

        /*
        检查微博数量
         */
        if (Constants.CHECK_WEIBO_NUM) {
            Element element = doc.getElementsByClass("tc").get(0);
            int startIndex = element.text().indexOf("[");
            int endIndex = element.text().indexOf("]");
            int weiboNum = Integer.parseInt(element.text().substring(startIndex + 1, endIndex));
            if (weiboNum > Constants.WEIBO_NO_MORE_THAN) {
                logger.info("Number of weibos is too large: " + weiboNum);
                return null;
            }
        }

        /*
        检查微博数量
         */
        Elements elements = doc.getElementsByClass("c");
        for (Element el : elements) {
            if (el.id().startsWith("M_")) {
                weiboItems.add(el);
            }
        }

        return weiboItems;
    }

    /*
    解析微博的HTML DIV结构，提取微博ID、内容等信息，创建Weibo对象
     */
    private static Weibo parse(Element weiboEl, String poster) {
        Weibo weibo = new Weibo();
        List<Element> subDivs = weiboEl.children();

        try {
            int subDivsSize = subDivs.size();
            weibo.setId(weiboEl.attr("id").substring(2));
            weibo.setPoster(poster);
            weibo.setPostTime(Utils.parseDate(weiboEl.getElementsByClass("ct").get(0).text().split("来自")[0]));

            if (subDivsSize == 1) {
                /*
                原创发布无附件微博
                 */
                weibo.setRepost(false);
                weibo.setHasPic(false);
                weibo.setContent(weiboEl.getElementsByClass("ctt").get(0).text());
            } else if (subDivsSize == 2) {
                if (subDivs.get(0).toString().contains("<span class=\"cmt\">原文转发")) {
                    /*
                    转发无附件微博
                     */
                    weibo.setRepost(true);
                    weibo.setHasPic(false);
                    weibo.setContent(getRepostReason(subDivs.get(1)));
                } else {
                    /*
                    原创发布带附件微博
                     */
                    weibo.setRepost(false);
                    weibo.setHasPic(true);
                    weibo.setContent(weiboEl.getElementsByClass("ctt").get(0).text());
                }
            } else if (subDivsSize == 3) {
                /*
                转发带附件的微博
                 */
                weibo.setRepost(true);
                weibo.setHasPic(true);
                weibo.setContent(getRepostReason(subDivs.get(2)));
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            weibo = null;
            logger.error("", e);
            logger.error("Not a valid weibo item: " + weiboEl);
        }

        return weibo;
    }

    /**
     * 从子div中获取转发原因
     *
     * @param processEl
     * @return
     */
    private static String getRepostReason(Element processEl) {
        StringBuilder repostReason = new StringBuilder();
        int endIndex = processEl.childNodes().size() - BACK_NODES_NUM_IN_REPOST_DIV;

        for (int i = 1; i < endIndex; i++) {
            repostReason.append(processEl.childNode(i).toString());
        }

        return repostReason.toString();
    }

    /**
     * 将抓取的微博信息保存至本地文件
     *
     * @param weiboItems
     * @param urlPath
     */
    public static void createFile(List<Element> weiboItems, String urlPath) {
        String userID = Utils.getUserIdFromUrl(urlPath);

        /*
        解析每一条微博，存入数据库
         */
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO weibo (accountID, weiboID, content, postTime) VALUES (?, ?, ?, ?)");
            for (Element el : weiboItems) {
                Weibo weibo = WeiboParser.parse(el, userID);
                ps.setString(1, userID);
                ps.setString(2, weibo.getId());
                ps.setString(3, weibo.getContent());
                ps.setString(4, weibo.getPostTime());
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException e) {
            logger.error("", e);
        }
    }
}
