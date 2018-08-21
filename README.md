```
大三时写的java课程设计，已停更
```

# zeekEye
### A Fast and Powerful Scraping and Web Crawling Framework

[![build](https://img.shields.io/teamcity/http/teamcity.jetbrains.com/s/bt345.svg)](https://github.com/crazyacking/zeekEye)
[![module](https://img.shields.io/puppetforge/mc/camptocamp.svg)](https://github.com/crazyacking/zeekEye)
[![license](https://img.shields.io/crates/l/rustc-serialize.svg)](https://github.com/crazyacking/zeekEye)

<img src="http://images2015.cnblogs.com/blog/606573/201609/606573-20160925160652775-985449199.png" alt="" width="650" height="401">


**zeekEye**是一款轻量级垂直爬虫，针对但不限于新浪微博，采用`Java`语言开发，基于`hetrix`架构，使用`HTTPClient4.0`和`Apache4.0`网络包.

特点概述：

- **数据存储**：采用`MySQL`数据库存储数据，支持多线程并发操作.

- **功能实现**：模拟微博登录、爬取微博用户信息、用户评论、提取数据、建立数据表、数据成份分析。待更新...

- **待实现**：互粉推荐、情感分析、数据聚类.

------欢迎 Fork !

-------------------
<div><div class="toc"><div class="toc">
<ul>
<li><a href="#zeekeye">zeekEye</a><ul>
<li><a href="#programmable-spidering-of-web-sites-with-java">– Programmable spidering of web sites with Java</a></li>
<li><a href="#运行">运行</a></li>
<li><a href="#api如何使用">API(如何使用)</a><ul>
<li><a href="#project config">project config</a><ul>
<li><a href="#weibo-spider选项">weibo-Spider(选项)</a></li>
</ul>
<li><a href="#爬虫抓取url队列">爬虫抓取url队列.</a></li>
<li><a href="#拓展-更新缓存">拓展 / 更新缓存</a></li>
<li><a href="#设置冗余日志级别">设置冗余/日志级别</a></li>
<li><a href="#source-code">Source Code</a></li>
</ul>
</li>
<li><a href="#反馈与建议">反馈与建议</a></li>
</ul>
</li>
</ul>

## 运行

``` bash
  git clone https://github.com/crazyacking/zeekEye.git
  cd zeekEye
  mvn compile
  mvn exec:java -Dexec.mainClass="SpiderStarter" 
  ...
```
默认编辑器是IntelliJ IDEA 14.1.4，开发环境为jdk1.7.0，编译执行前先用IntelliJ IDEA把项目源码导出成jar包.
## API(如何使用)
### project config
``` bash
  conf/spider.properties文件为整个项目相关参数的配置文件，包括数据库接口地址、并行线程、爬取数量上限的配置等.
```

#### weibo-Spider(选项)

"选项"包含以下字段：
* `maxSockets` - 线程池中最大并行线程数. 默认为 `4`.
* `userAgent` - 发送到远程服务器的用户代理请求. 默认为 `Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-US)’
* `pool` - 一个包含该请求代理的哈希线程池。如果省略，将使用全局设置的maxsockets.

### 添加路由处理程序

#### spider.route(主机，模式)
其中参数如下 :

* `hosts` - string类型 -- 或是一个数组类型 -- 目标主机的url.

### 爬虫抓取url队列.

`spider.get(url)`其中'url'是要抓取的网络url.

### 拓展 / 更新缓存

目前更新缓存暂提供以下方法:

* `get(url, cb)` - 如果url已存在,通过 `cb` 回调函数返回 `url`'的`body`. 否则返回'null'.
  * `cb` - 固定形式 `function(retval) {...}'
* `getHeaders(url, cb)` - 如果url已经存在,返回`url`的 `headers`,否则返回`null`.
  * `cb` - 固定格式 `function(retval) {...}`
* `set(url, headers, body)` - 设置/保存 `url`的 `headers` 和 `body`.

### 设置冗余/日志级别
`spider.log(level)` - 这儿的`level`是一个string，可以是`"debug"`, `"info"`, `"error"`

### Source Code

The source code of zeekEye is made available for study purposes only. Neither it, its source code, nor its byte code may be modified and recompiled for public use by anyone except us.

We do accept and encourage private modifications with the intent for said modifications to be added to the official public version.


## 反馈与建议
- 微博：[@crazyacking](http://weibo.com/u/3736544454)
- 邮箱：<crazyacking@gmail.com>

---------
感谢阅读这份帮助文档。如果您有好的建议，欢迎反馈。
