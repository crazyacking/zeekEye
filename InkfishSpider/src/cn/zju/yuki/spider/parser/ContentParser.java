package cn.zju.yuki.spider.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.VisitedUrlQueue;

import java.io.*;

public class ContentParser {
	public Object parse(FetchedPage fetchedPage) throws FileNotFoundException {
		Object targetObject = null;
		Document doc = Jsoup.parse(fetchedPage.getContent());
		
		// 如果当前页面包含目标数据
		if(containsTargetData(fetchedPage.getUrl(), doc)){
			// 解析并获取目标数据
			// TODO
		}
		
		// 将URL放入已爬取队列
		VisitedUrlQueue.addElement(fetchedPage.getUrl());
//			FileOutputStream out = new FileOutputStream("Test/TestFileOut.out");
//			PrintStream p = new PrintStream(out);
//			p.println(fetchedPage.getUrl());


		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return targetObject; 
	}
	
	private boolean containsTargetData(String url, Document contentDoc) throws FileNotFoundException {
		// 通过URL判断
		// TODO
		
		// 通过content判断，比如需要抓取class为grid_view中的内容

		FileOutputStream out=new FileOutputStream("Test/TestFileOut.out");
		PrintStream p=new PrintStream(out);
		if(contentDoc.getElementsByClass("grid_view") != null){
			p.println(contentDoc.getElementsByClass("grid_view").toString());
			return true;
		}
		return false;
	}
}
