package cn.jkdev.host.utils;

/**
 * Created by pan on 17-8-30.
 */

public class ConstantValues {

    public static final String URL_NORMAL_HOSTS = "http://47.95.205.197:8080/Host/recovery/hosts";//Android默认hosts文件地址
    public static final String URL_GET_GOOGLE_HOSTS = "http://47.95.205.197:8080/Host/hosts";//科学上网hosts文件地址
    public static final String URL_GET_UPDATE_MESSAGE = "http://47.95.205.197:8080/Host/update.json";//更新信息地址
    public static final java.lang.String URL_GET_NEW_APP_MESSAGE = "http://47.95.205.197:8080/Host/app_update.json";
    /*
    * 保存在SharedPreference中的键
    * */
    public static final String UPDATED_DATE = "updated_date";//已经更新的版本日期
    public static final String HAVE_HOSTS = "have_hosts";//是否安装了hosts文件
    public static final String AUTO_UPDATE = "auto_update";//是否自动更新
    public static final String SAVE_FILE_PATH = "/sdcard/host科学上网.apk";
}
