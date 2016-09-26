package com.alibaba.aliyun.crazyacking.spider.fetcher;

import com.alibaba.aliyun.crazyacking.spider.parser.bean.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Mengyu on 2016/9/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:utils-beans.xml"})
public class CommentFetcherTest {

    @Resource(name = "fetcher")
    CommentFetcher fetcher;

    @Test
    public void getContentFromUrl() throws Exception {
        Page page = fetcher.getContentFromUrl("www.cnblogs.com");
        System.out.println(page.getContent());
    }

}