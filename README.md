# zeekEye
##-- Programmable spidering of web sites with Java


[![build](https://img.shields.io/codeship/d6c1ddd0-16a3-0132-5f85-2e35c05e22b1/master.svg)](https://github.com/crazyacking/Spider--Java/tree/master/src/cn/edu/hut/crazyacking/spider)
[![module](https://img.shields.io/puppetforge/mc/camptocamp.svg)](https://github.com/crazyacking/Spider--Java/tree/master/src/cn/edu/hut/crazyacking/spider)
[![license](https://img.shields.io/crates/l/rustc-serialize.svg)](https://github.com/crazyacking/Spider--Java/tree/master/.idea)

**zeekEye**是一款新浪微博爬虫，采用`Java`语言开发，基于`hetrix`爬虫架构,使用`HTTPClient4.0`和`Apache4.0`网络包.

特点概述：

- **数据存储**：采用`MySQL`数据库存储数据，支持多线程并发操作.

- **功能实现**：模拟微博登录、爬取微博用户信息、用户评论、提取数据、建立数据表、数据成份分析、互粉推荐。待更新... 

------欢迎 Fork !

-------------------
<div><div class="toc"><div class="toc">
<ul>
<li><a href="#zeekeye">zeekEye</a><ul>
<li><a href="#programmable-spidering-of-web-sites-with-java">– Programmable spidering of web sites with Java</a></li>
<li><a href="#安装">安装</a></li>
<li><a href="#api如何使用">API(如何使用)</a><ul>
<li><a href="#project config">Creating a Spider</a><ul>
<li><a href="#weibo-spider选项">weibo-Spider(选项)</a></li>
</ul>
</li>
<li><a href="#添加路由处理程序">添加路由处理程序</a><ul>
<li><a href="#spiderroute主机模式">spider.route(主机，模式)</a></li>
</ul>
</li>
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

## 安装

``` python
  git clone git@github.com:crazyacking/Spider--Java.git
  javac -cp /home/username/Documents/Spider--Java/src/cn/edu/hut/crazyacking/spider/Spider.jar  WeiboSpiderStarter.java
  java -cp /home/username/Documents/Spider--Java/src/cn/edu/hut/crazyacking/spider/Spider.jar : WeiboSpiderStarter
  ...
```
默认编辑器是IntelliJ IDEA 14.1.4，开发环境为jdk1.7.0，编译执行前先用IntelliJ IDEA把项目源码导出成jar包.
## API(如何使用)
### project config
``` python
  conf/spider.properties文件为整个项目相关参数的配置文件，包括数据库接口地址、并行线程、爬取数量上限的配置等.
```

#### weibo-Spider(选项)

"选项"包含以下字段：
* `maxSockets` - 线程池中最大并行线程数. 默认为 `4`.
* `userAgent` - 发送到远程服务器的用户代理请求. 默认为 `Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Chrome/7.0.517.41 Safari/534.7` (firefox userAgent String).
* `cache` -  缓存对象。默认为非缓存，具体看最新版本代码缓存对象的实现细节.
* `pool` - 一个包含该请求代理的哈希线程池。如果省略，将使用全局设置的maxsockets.

### 添加路由处理程序

#### spider.route(主机，模式)
其中参数如下 :

* `hosts` - A string -- or an array of string -- representing the `host` part of the targeted URL(s).
* `pattern` - The pattern against which spider tries to match the remaining (`pathname` + `search` + `hash`) of the URL(s).
* `cb` - A function of the form `function(window, $)` where
  * `this` - Will be a variable referencing the `Routes.match` return object/value with some other goodies added from spider. For more info see http://www.cnblogs.com/crazyacking/category/686354.html
  * `window` - Will be a variable referencing the document's window.
  * `$` - Will be the variable referencing the jQuery Object.

### 爬虫抓取url队列.

`spider.get(url)`其中'url'是要抓取的网络url.

### 拓展 / 更新缓存

目前更新缓存暂提供以下方法:

* `get(url, cb)` - Returns `url`'s `body` field via the `cb` callback/continuation if it exists. Returns `null` otherwise.
  * `cb` - Must be of the form `function(retval) {...}'
* `getHeaders(url, cb)` - Returns `url`'s `headers` field via the `cb` callback/continuation if it exists. Returns `null` otherwise.
  * `cb` - Must be of the form `function(retval) {...}`
* `set(url, headers, body)` - Sets/Saves `url`'s `headers` and `body` in the cache.

### 设置冗余/日志级别
`spider.log(level)` - Where `level` is a string that can be any of `"debug"`, `"info"`, `"error"`

###Source Code
The source code of zeekEye is made available for study purposes only. Neither it, its source code, nor its byte code may be modified and recompiled for public use by anyone except us.

We do accept and encourage private modifications with the intent for said modifications to be added to the official public version.


## 反馈与建议
- 微博：[@crazyacking](http://weibo.com/u/3736544454)
- 邮箱：<crazyacking@gmail.com>

---------
感谢阅读这份帮助文档。如果您有好的建议，欢迎反馈。
