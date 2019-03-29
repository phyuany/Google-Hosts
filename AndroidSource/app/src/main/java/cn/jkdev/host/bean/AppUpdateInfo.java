package cn.jkdev.host.bean;

/**
 * Created by pan on 17-9-1.
 */

public class AppUpdateInfo {
    public String versionName;
    public String updateInfo;
    public String updateUrl;

    @Override
    public String toString() {
        return "AppUpdateInfo{" +
                "versionName='" + versionName + '\'' +
                ", updateInfo='" + updateInfo + '\'' +
                ", updateUrl='" + updateUrl + '\'' +
                '}';
    }
}
