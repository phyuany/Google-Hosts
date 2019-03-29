# Google-Hosts
hosts列表很多失效了

## 停止更新了，公开Android源代码

完全停止更新了，并且公开Android源代码，核心代码如下

~~~java
package cn.jkdev.host.utils;

/**

- Created by pan on 17-8-30.
  */

public class ConstantValues {

```
public static final String URL_NORMAL_HOSTS = "http://47.95.205.197:8080/Host/recovery/hosts";//Android默认hosts文件地址
public static final String URL_GET_GOOGLE_HOSTS = "http://47.95.205.197:8080/Host/hosts";//科学上网hosts文件地址
public static final String URL_GET_UPDATE_MESSAGE = "http://47.95.205.197:8080/Host/update.json";//更新信息地址
public static final java.lang.String URL_GET_NEW_APP_MESSAGE = "http://47.95.205.197:8080/Host/app_update.json";//更新信息
/*
* 保存在SharedPreference中的键
* */
public static final String UPDATED_DATE = "updated_date";//已经更新的版本日期
public static final String HAVE_HOSTS = "have_hosts";//是否安装了hosts文件
public static final String AUTO_UPDATE = "auto_update";//是否自动更新
public static final String SAVE_FILE_PATH = "/sdcard/host科学上网.apk";
```

}
~~~

服务器端更新信息的JSON文件示例如下

```json
{

date:"2019.03.29",

content:"这是更新内容介绍"

}
```





下载客户端

<p><a href="https://github.com/jkdev-cn/Google-Hosts/raw/master/Google-Hosts.apk">Android客户端1.2.0下载</a></p>

<p><a href="https://github.com/jkdev-cn/Google-Hosts/raw/master/windows.zip">windows脚本下载</a></p>


补充：
## 分享其他方式：
（1）一个开源代理工具：<a href="https://github.com/yinghuocho/firefly-proxy">https://github.com/yinghuocho/firefly-proxy</a><br>
（2）代理服务器列表（需要科学上网）：<a href="http://www.gatherproxy.com/zh/sockslist/">http://www.gatherproxy.com/zh/sockslist/</a>


## 1.关于此项目与hosts
本项目为开源免费项目，hosts解析记录列表来源互联网，Android　APP客户端和windows的bat脚本由本人编写，不存在任何盈利，大家要理性辨认，不要被他人利用作为索财之道，本项目的主体功能是通过Android APP实现，本人完全是因为Android APP开发的兴趣而维护此项目，完全是个人的兴趣

(1)熟悉计算机的同学都知道，hosts文件是用于解析域名的，它会把我们在上网时输入的域名解析成对应的IP地址，因此我们可以访问到很多网站，但是通常情况下我们是不使用hosts文件解析域名的，因为互联网上有大量的域名大量的IP，所以一般上网时浏览器会先请求在互联网节点上的域名服务器，然后我们就可以顺利访问到我们访问的网站了

(2)但是，域名服务器也不是能对所有域名解析成对应IP，因此，我们的hosts解析文件就起作用了，如果我们操作系统内部的hosts文件包含了一个域名的解析记录，那么我们上网时首先通过本地解析，如果本地没有解析记录，浏览器再请求域名服务器通过域名服务器来解析，因此我们都知道hosts文件存在的意义，如软件测试，如本地解析

(3)Google-Hosts为开源项目，自己为了学术的需求而收集此hosts解析文件，完全供自己使用，请不要使用此项目进行传播甚至非法使用，任何非法行为您自负，与本项目无关，使用此hosts文件，可以解析到Google搜索，Google学术，Facebook,Twitter,IG等，访问大多网站需使用https加密访问，访问谷歌时最好在域名末尾添加“/ncr”，代表无国别跳转，即“https://www.google.com/ncr”.

## 2.windows使用方法：
解压windows.zip后，点击＂．bat＂脚本文件运行即可实现hosts文件复制到您的windows系统，如图所示

(1)执行脚本
<img src="https://github.com/jkdev-cn/Google-Hosts/blob/master/res/windows_01.png">

(2)hosts复制成功
<img src="https://github.com/jkdev-cn/Google-Hosts/blob/master/res/windows_02.png">

## 3.Android客户端使用方法：
下载Google-Hosts.apk进行安装，手机需要root权限，点击一键科学上网，即可实现最新hosts复制到您的Android系统
<p><a href="https://github.com/jkdev-cn/Google-Hosts/raw/master/res/video.mp4">Android客户端演示视频下载</a></p>

<img src="https://github.com/jkdev-cn/Google-Hosts/blob/master/res/mobile_1.png" width = "324" height = "576" alt="图片" align=center />　<img src="https://github.com/jkdev-cn/Google-Hosts/blob/master/res/mobile_2.png" width = "324" height = "576" alt="图片" align=center />
<br>

免责声明：本项目为作者学习项目，你的参考、安装等等，出现的任何问题与作者无关


