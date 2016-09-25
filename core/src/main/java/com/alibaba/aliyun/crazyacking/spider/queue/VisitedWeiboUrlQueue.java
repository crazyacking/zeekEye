package com.alibaba.aliyun.crazyacking.spider.queue;

import java.util.HashSet;

/**
 * 已访问url队列
 * @author crazyacking
 *
 */
public class VisitedWeiboUrlQueue {
	private static final HashSet<String> visitedUrlQueue = new HashSet<String>();
	private static int count = 0;
	
	public synchronized static void addElement(String url){
		// visitedUrlQueue.add(url);
		count++;
	}
	
	public synchronized static boolean isContains(String url){
		return visitedUrlQueue.contains(url);
	}
	
	public synchronized static int size(){
		// return visitedUrlQueue.size();
		return count;
	}
}
