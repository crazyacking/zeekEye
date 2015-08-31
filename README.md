
<!DOCTYPE html><html><head><title>zeekEye</title><meta charset='utf-8'><link href='https://dn-maxiang.qbox.me/res-min/themes/marxico.css' rel='stylesheet'></head><body><div id='preview-contents' class='note-content'>
                        <div id="wmd-preview" class="preview-content"></div>
                    <div id="wmd-preview-section-382" class="wmd-preview-section preview-content">

</div><div id="wmd-preview-section-383" class="wmd-preview-section preview-content">

<h1 id="zeekeye">zeekEye</h1>

</div><div id="wmd-preview-section-3116" class="wmd-preview-section preview-content">

<h2 id="programmable-spidering-of-web-sites-with-java">– Programmable spidering of web sites with Java</h2>

<p>网络爬虫|新浪微博|数据分析|帮助</p>

<p><strong>zeekEye</strong>是一款新浪微博爬虫，采用<code>Java</code>语言开发，基于<code>hetrix</code>爬虫架构,使用<code>HTTPClient4.0</code>和<code>Apache4.0</code>网络包.</p>

<p>特点概述：</p>

<ul><li><p><strong>数据存储</strong>：采用<code>SQL Server</code>数据库存储数据，支持多线程并发操作.</p></li>
<li><p><strong>功能实现</strong>：模拟微博登录、爬取微博用户信息、用户评论、提取数据、建立数据表、数据成份分析、互粉推荐。待更新… </p></li>
</ul>

<p>欢迎Fork ! </p>

<hr>

<hr>

<div><div class="toc"><div class="toc">
<ul>
<li><a href="#zeekeye">zeekEye</a><ul>
<li><a href="#programmable-spidering-of-web-sites-with-java">– Programmable spidering of web sites with Java</a></li>
<li><a href="#安装">安装</a></li>
<li><a href="#api如何使用">API(如何使用)</a><ul>
<li><a href="#creating-a-spider">Creating a Spider</a><ul>
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
</div>
</div>
</div></div><div id="wmd-preview-section-2666" class="wmd-preview-section preview-content">

<h2 id="安装">安装</h2>

</div><div id="wmd-preview-section-2667" class="wmd-preview-section preview-content">

<pre class="prettyprint hljs-dark"><code class="language-python hljs">  git clone git<span class="hljs-decorator">@github.com:crazyacking/Spider--Java.git</span><br>  cd Spider--Java<br></code></pre>

<p>默认编辑器是IntelliJ IDEA 14.1.4，Eclipse也能完美运行.</p>

</div><div id="wmd-preview-section-2668" class="wmd-preview-section preview-content">

<h2 id="api如何使用">API(如何使用)</h2>

</div><div id="wmd-preview-section-2669" class="wmd-preview-section preview-content">

<h3 id="creating-a-spider">Creating a Spider</h3>

</div><div id="wmd-preview-section-2670" class="wmd-preview-section preview-content">

<pre class="prettyprint hljs-dark"><code class="language-python hljs">  var spider = require(<span class="hljs-string">'spider'</span>);<br>  var s = spider();<br></code></pre>

</div><div id="wmd-preview-section-2671" class="wmd-preview-section preview-content">

<h4 id="weibo-spider选项">weibo-Spider(选项)</h4>

<p>“选项”包含以下字段：</p>

<ul><li><code>maxSockets</code> - 线程池中最大并行线程数. 默认为 <code>4</code>.</li>
<li><code>userAgent</code> - 发送到远程服务器的用户代理请求. 默认为 <code>Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Chrome/7.0.517.41 Safari/534.7</code> (firefox userAgent String).</li>
<li><code>cache</code> -  缓存对象。默认为非缓存，具体看最新版本代码缓存对象的实现细节.</li>
<li><code>pool</code> - 一个包含该请求代理的哈希线程池。如果省略，将使用全局设置的maxsockets.</li>
</ul>

</div><div id="wmd-preview-section-2672" class="wmd-preview-section preview-content">

<h3 id="添加路由处理程序">添加路由处理程序</h3>

</div><div id="wmd-preview-section-2673" class="wmd-preview-section preview-content">

<h4 id="spiderroute主机模式">spider.route(主机，模式)</h4>

<p>其中参数如下 :</p>

<ul><li><code>hosts</code> - A string – or an array of string – representing the <code>host</code> part of the targeted URL(s).</li>
<li><code>pattern</code> - The pattern against which spider tries to match the remaining (<code>pathname</code> + <code>search</code> + <code>hash</code>) of the URL(s).</li>
<li><code>cb</code> - A function of the form <code>function(window, $)</code> where <br>
<ul>
<li><code>this</code> - Will be a variable referencing the <code>Routes.match</code> return object/value with some other goodies added from spider. For more info see <a href="http://www.cnblogs.com/crazyacking/category/686354.html" target="_blank">http://www.cnblogs.com/crazyacking/category/686354.html</a></li>
<li><code>window</code> - Will be a variable referencing the document’s window.</li>
<li><code>$</code> - Will be the variable referencing the jQuery Object.</li></ul></li>
</ul>

</div><div id="wmd-preview-section-2674" class="wmd-preview-section preview-content">

<h3 id="爬虫抓取url队列">爬虫抓取url队列.</h3>

<p><code>spider.get(url)</code>其中’url’是要抓取的网络url.</p>

</div><div id="wmd-preview-section-2675" class="wmd-preview-section preview-content">

<h3 id="拓展-更新缓存">拓展 / 更新缓存</h3>

<p>目前更新缓存暂提供以下方法:</p>

<ul><li><code>get(url, cb)</code> - Returns <code>url</code>’s <code>body</code> field via the <code>cb</code> callback/continuation if it exists. Returns <code>null</code> otherwise. <br>
<ul>
<li><code>cb</code> - Must be of the form `function(retval) {…}’</li></ul></li>
<li><code>getHeaders(url, cb)</code> - Returns <code>url</code>’s <code>headers</code> field via the <code>cb</code> callback/continuation if it exists. Returns <code>null</code> otherwise. <br>
<ul>
<li><code>cb</code> - Must be of the form <code>function(retval) {...}</code></li></ul></li>
<li><code>set(url, headers, body)</code> - Sets/Saves <code>url</code>’s <code>headers</code> and <code>body</code> in the cache.</li>
</ul>

</div><div id="wmd-preview-section-2676" class="wmd-preview-section preview-content">

<h3 id="设置冗余日志级别">设置冗余/日志级别</h3>

<p><code>spider.log(level)</code> - Where <code>level</code> is a string that can be any of <code>"debug"</code>, <code>"info"</code>, <code>"error"</code></p>

</div><div id="wmd-preview-section-2869" class="wmd-preview-section preview-content">

<h3 id="source-code">Source Code</h3>

<p>The source code of zeekEye is made available for study purposes only. Neither it, its source code, nor its byte code may be modified and recompiled for public use by anyone except us.</p>

<p>We do accept and encourage private modifications with the intent for said modifications to be added to the official public version.</p></div><div id="wmd-preview-section-3161" class="wmd-preview-section preview-content">

<h2 id="反馈与建议">反馈与建议</h2>

<ul><li>微博：<a href="http://weibo.com/u/3736544454" target="_blank">@crazyacking</a></li>
<li>邮箱：<a href="mailto:crazyacking@gmail.com" target="_blank">crazyacking@gmail.com</a></li>
</ul>

<hr>

<p>感谢阅读这份帮助文档。如果您有好的建议，欢迎反馈。</p></div><div id="wmd-preview-section-footnotes" class="preview-content"></div></div></body></html>
