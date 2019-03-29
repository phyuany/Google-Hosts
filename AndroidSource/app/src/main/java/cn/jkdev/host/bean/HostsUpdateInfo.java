package cn.jkdev.host.bean;

/**
 * Created by pan on 17-8-30.
 */

public class HostsUpdateInfo {
    public String date;//版本号（日期代替）
    public String content;//更新内容描述

    @Override
    public String toString() {
        return "date" + date + "\n" + "content" + content;
    }
}
