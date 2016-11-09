package com.alibaba.aliyun.crazyacking.spider.parser.bean;

public class Weibo {
    private String id;
    private String poster;
    private String content;
    private String postTime;
    private boolean hasPic;
    private boolean repost;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public boolean isHasPic() {
        return hasPic;
    }

    public void setHasPic(boolean hasPic) {
        this.hasPic = hasPic;
    }

    public boolean isRepost() {
        return repost;
    }

    public void setRepost(boolean repost) {
        this.repost = repost;
    }

    @Override
    public String toString() {
        String sb = "id:\t\t" + id + "\n" +
                "poster:\t\t" + poster + "\n" +
                "content:\t" + content + "\n" +
                "hasPic:\t\t" + hasPic + "\n" +
                "repost:\t" + repost + "\n";

        return sb;
    }
}
