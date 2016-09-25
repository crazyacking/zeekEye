package com.alibaba.aliyun.crazyacking.spider.parser.bean;


public class RePost {
    private String id = null;
    private String author = null;
    private String time = null;
    private String content = null;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String toString() {
        String sb = "id:\t\t" + id + "\n" +
                "author:\t\t" + author + "\n" +
                "content:\t" + content + "\n" +
                "time:\t\t" + time + "\n";

        return sb;
    }
}

