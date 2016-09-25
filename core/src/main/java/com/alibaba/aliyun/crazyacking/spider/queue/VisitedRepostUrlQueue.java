package com.alibaba.aliyun.crazyacking.spider.queue;

import java.util.HashSet;

/**
 * 已访问url队列
 *
 * @author crazyacking
 */
public class VisitedRepostUrlQueue {
    private static final HashSet<String> visitedRepostUrlQueue = new HashSet<String>();
    private static int count = 0;

    public synchronized static void addElement(String url) {
        // visitedRepostUrlQueue.add(url);
        count++;
    }

    public synchronized static boolean isContains(String url) {
        return visitedRepostUrlQueue.contains(url);
    }

    public synchronized static int size() {
        // return visitedRepostUrlQueue.size();
        return count;
    }
}
