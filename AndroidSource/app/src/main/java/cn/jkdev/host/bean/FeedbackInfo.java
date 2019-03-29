package cn.jkdev.host.bean;

import android.widget.EditText;

/**
 * 反馈信息类
 * Created by pan on 17-8-23.
 */

public class FeedbackInfo {
    public String address;//用户反馈留下的地址
    public String content;//反馈内容

    public FeedbackInfo(String address, String content) {
        this.address = address;
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public String getContent() {
        return content;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
