package com.alibaba.aliyun.crazyacking.spider.queue;

import java.util.HashSet;

/**
 * 已访问url队列
 * @author crazyacking
 *
 */
public class VisitedCommentUrlQueue {
	private static final HashSet<String> visitedCommentUrlQueue = new HashSet<String>();
	private static int count = 0;
	
	public synchronized static void addElement(String url){
		// visitedCommentUrlQueue.add(url);
		count++;
	}
	
	public synchronized static boolean isContains(String url){
		return visitedCommentUrlQueue.contains(url);
	}
	
	public synchronized static int size(){
		// return visitedCommentUrlQueue.size();
		return count;
	}
}
