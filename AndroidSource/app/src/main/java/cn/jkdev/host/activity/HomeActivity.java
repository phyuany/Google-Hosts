package cn.jkdev.host.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.stericson.RootTools.RootTools;

import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import cn.jkdev.host.BuildConfig;
import cn.jkdev.host.R;
import cn.jkdev.host.bean.AppUpdateInfo;
import cn.jkdev.host.bean.HostsUpdateInfo;
import cn.jkdev.host.utils.ConstantValues;
import cn.jkdev.host.utils.SPUtils;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    * 对操作内容进行标志
    * */
    private static final int UPDATE_HOSTS = 101;//更新hosts
    private static final int RECOVERY_HOSTS = 102;//恢复hosts
    private int OPERATE = UPDATE_HOSTS;//默认标记操作内容为更新hosts

    /*
    * 对操作状态进行标志
    * */
    private static final int UPLOAD_FINISHED = 201;//下载成功
    private static final int UPLOAD_ERROR = 202;//下载失败
    private static final int COPY_ERROR = 203;//复制失败
    private static final int COPY_FINISHED = 204;//复制成功

    private LinearLayout mStateDes;//安装过程的描述控件集合
    private TextView mUpdateContent;//用于显示更新内容
    private TextView mHostsState, mCheckRoot, mDownloadFile, mCopyFile;//状态显示文本
    private ProgressBar mProgress;//进度条
    private Button mUpdateButton;//更新按钮

    private ProgressDialog progressDialog;//更新进度条

    /**
     * 可取消的任务
     */
    private Callback.Cancelable cancelable;

    private HostsUpdateInfo updateInfo;//更新内容对象

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOAD_FINISHED:
                    //下载hosts文件成功
                    mDownloadFile.setVisibility(View.VISIBLE);
                    //复制hosts文件
                    copyHostsToSystemEtcDir();
                    break;
                case UPLOAD_ERROR:
                    //下载失败
                    mDownloadFile.setVisibility(View.VISIBLE);
                    //更改文字
                    mDownloadFile.setTextColor(Color.RED);
                    mDownloadFile.setText("失败");
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    //使进度条消失
                    mProgress.setVisibility(View.INVISIBLE);
                    //结束程序
                    return;
                case COPY_ERROR:
                    //复制失败
                    mCopyFile.setVisibility(View.VISIBLE);
                    mCopyFile.setTextColor(Color.RED);
                    mCopyFile.setText("失败");
                    //使进度条消失
                    mProgress.setVisibility(View.INVISIBLE);
                    break;
                case COPY_FINISHED:
                    //复制成功
                    mCopyFile.setVisibility(View.VISIBLE);
                    //使进度条消失
                    mProgress.setVisibility(View.INVISIBLE);
                    //保存状态
                    switch (OPERATE) {
                        case UPDATE_HOSTS:
                            //更新hosts
                            SPUtils.putBoolean(getApplicationContext(), ConstantValues.HAVE_HOSTS, true);
                            //保存最后一次更新的版本（以日期代替）
                            if (updateInfo != null) {
                                SPUtils.putString(getApplicationContext(), ConstantValues.UPDATED_DATE, updateInfo.date);
                            }
                            //回显hosts状态
                            showHostStateInfo();
                            //提示是否访问谷歌
                            showDialogIfVisitGoogle();
                            break;
                        case RECOVERY_HOSTS:
                            //恢复hosts
                            SPUtils.putBoolean(getApplicationContext(), ConstantValues.HAVE_HOSTS, false);
                            //保存空字符串
                            SPUtils.putString(getApplicationContext(), ConstantValues.UPDATED_DATE, "");
                            //回显hosts状态
                            showHostStateInfo();
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void showDialogIfVisitGoogle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setPositiveButton()
        final AlertDialog alertDialog = builder.create();
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setTitle("恭喜你！");
        alertDialog.setMessage("成功安装科学上网hosts，请使用https加密访问，是否立即访问Google？");

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //访问谷歌
                Uri uri = Uri.parse("https://www.google.com/ncr");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));

                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    //复制hosts文件
    private void copyHostsToSystemEtcDir() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isCopy = RootTools.copyFile("/sdcard/hosts", "/system/etc", true, true);
                Log.i("tag", "文件复制是否完成：" + isCopy);
                Message message = Message.obtain();
                if (isCopy) {
                    //复制成功
                    message.what = COPY_FINISHED;
                } else {
                    //复制失败
                    message.what = COPY_ERROR;
                }
                //复制操作后删除下载的hosts文件
                File file = new File("sdcard/hosts");
                if (file.exists()) {
                    boolean delete = file.delete();
                    if (delete) {
                        Log.i("tag", "删除成功");
                    } else {
                        Log.i("tag", "删除成功");
                    }
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //初始化控件
        initView();
        //初始化数据
        initData();
    }

    //初始化数据
    private void initData() {
        //初始化xUtils3工具
        x.Ext.init(getApplication());
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

        //请求权限
        requestPermission();

        //获取服务器更新数据
        getJsonFromServer();

        //回显Hosts状态
        showHostStateInfo();

        //准备更新
        if (SPUtils.getBoolean(getApplicationContext(), ConstantValues.AUTO_UPDATE, true)) {
            //获取服务器更新信息
            getUpdateInfoFromServer();
        }
    }

    private void requestPermission() {
        int REQUEST_EXTERNAL_STORAGE = 1;//请求状态码
        //请求权限集合
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permission = ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    HomeActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //初始化控件
    private void initView() {
        //设置toolbar的支持
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //获取安装过程状态的描述信息
        mStateDes = (LinearLayout) findViewById(R.id.ll_state_des);

        //获取文本控件
        mHostsState = (TextView) findViewById(R.id.tv_hosts_state);
        mUpdateContent = (TextView) findViewById(R.id.tv_content);
        mCheckRoot = (TextView) findViewById(R.id.tv_check_root);
        mDownloadFile = (TextView) findViewById(R.id.tv_download_file);
        mCopyFile = (TextView) findViewById(R.id.tv_copy_file);
        //获取按钮
        mUpdateButton = (Button) findViewById(R.id.bt_get_host);
        mUpdateButton.setOnClickListener(this);
        findViewById(R.id.tv_bt_recovery).setOnClickListener(this);
        //获取原型滚动空间
        mProgress = (ProgressBar) findViewById(R.id.pb_progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                //关于
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                break;
            case R.id.menu_feedback:
                //反馈
                startActivity(new Intent(getApplicationContext(), FeedBackActivity.class));
                break;
            case R.id.menu_setting:
                //设置界面
                startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        //1.点击按钮后让圆形滚动空间滚动
        mProgress.setVisibility(View.VISIBLE);
        //初始化文字显示内容
        initTextHintShow();

        //2.判断权限
        if (RootTools.isRootAvailable()) {
            //已经获取root权限
            mCheckRoot.setVisibility(View.VISIBLE);
        } else {
            //未获取root权限
            mCheckRoot.setVisibility(View.VISIBLE);
            //更改提示文字
            mCheckRoot.setTextColor(Color.RED);
            mCheckRoot.setText("失败");
            Toast.makeText(getApplicationContext(), "请检查root权限", Toast.LENGTH_LONG).show();
            //使进度条消逝
            mProgress.setVisibility(View.INVISIBLE);
            //退出逻辑
            return;
        }

        //3.判断按钮，并作出相应的逻辑
        switch (view.getId()) {
            case R.id.bt_get_host:
                //安装hosts
                updateHostsFile();
                break;
            case R.id.tv_bt_recovery:
                //提示是否恢复默认hosts
                showDialogIfRecoveryHosts();
                break;
            default:
                break;
        }
    }

    private void showDialogIfRecoveryHosts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setTitle("提示！");
        alertDialog.setMessage("恢复默认hosts后，将不能顺利访问谷歌等网站，是否确定？");
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //恢复hosts
                recoveryHostsFile();
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //初始化状态文本的默认状态
                initTextHintShow();
                //设置进度条不可见
                mProgress.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void initTextHintShow() {
        //描述文本集合设置可见
        mStateDes.setVisibility(View.VISIBLE);

        //使描述安装状态的TextView对应的状态TextView设置为不可见
        mCheckRoot.setText("已完成");
        mCheckRoot.setTextColor(Color.BLUE);
        mCheckRoot.setVisibility(View.INVISIBLE);

        mDownloadFile.setText("已完成");
        mDownloadFile.setTextColor(Color.BLUE);
        mDownloadFile.setVisibility(View.INVISIBLE);

        mCopyFile.setText("已完成");
        mCopyFile.setTextColor(Color.BLUE);
        mCopyFile.setVisibility(View.INVISIBLE);


    }

    /**
     * 恢复默认文件
     */
    private void recoveryHostsFile() {
        //下载默认的hosts文件
        downloadHostsFile(ConstantValues.URL_NORMAL_HOSTS);
        //标记操作内容
        OPERATE = RECOVERY_HOSTS;
    }


    /**
     * 复制hosts文件
     */
    private void updateHostsFile() {
        //下载科学上网的hosts文件
        downloadHostsFile(ConstantValues.URL_GET_GOOGLE_HOSTS);
        //标记操作内容
        OPERATE = UPDATE_HOSTS;
    }

    private void downloadHostsFile(String downloadURL) {
        //设置请求参数
        final RequestParams params = new RequestParams(downloadURL);
        params.setAutoResume(true);//设置是否在下载是自动断点续传
        params.setAutoRename(false);//设置是否根据头信息自动命名文件
        params.setSaveFilePath("/sdcard/hosts");
        params.setExecutor(new PriorityExecutor(2, true));//自定义线程池,有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
        params.setCancelFast(true);//是否可以被立即停止.

        //开启主线程进行下载
        Log.i("tag", "未下载时的线程" + Thread.currentThread().getName());

        cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onCancelled(Callback.CancelledException arg0) {
                Log.i("tag", "取消" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Log.i("tag", "onError: 失败" + Thread.currentThread().getName());
                //progressDialog.dismiss();
                Message message = Message.obtain();
                message.what = UPLOAD_ERROR;
                mHandler.sendMessage(message);
            }

            @Override
            public void onFinished() {
                Log.i("tag", "完成,每次取消下载也会执行该方法" + Thread.currentThread().getName());
                //progressDialog.dismiss();
            }

            @Override
            public void onSuccess(File arg0) {
                Log.i("tag", "下载成功的时候执行" + Thread.currentThread().getName());
                //下载成功之后更新文件
                Message message = Message.obtain();
                message.what = UPLOAD_FINISHED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if (isDownloading) {
                    //progressDialog.setProgress((int) (current*100/total));
                    Log.i("tag", "下载中,会不断的进行回调:" + Thread.currentThread().getName());
                }
            }

            @Override
            public void onStarted() {
                Log.i("tag", "开始下载的时候执行" + Thread.currentThread().getName());
                //progressDialog.show();
            }

            @Override
            public void onWaiting() {
                Log.i("tag", "等待,在onStarted方法之前执行" + Thread.currentThread().getName());
            }

        });
    }

    //获取服务器更新数据
    public void getJsonFromServer() {
        /*
        * 使用xUtils3开源框架
        * */
        //创建Request请求对象
        RequestParams params = new RequestParams(ConstantValues.URL_GET_UPDATE_MESSAGE);
        //发送request请求
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //请求成功
                Log.d("请求服务器的结果", result);
                //解析数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //请求失败
                ex.printStackTrace();
                Log.i("请求失败的息：", ex.toString());
                //提示失败信息
                Toast.makeText(getApplicationContext(), "服务器连接失败\n请检查您的网络连接", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /*
    * 解析数据
    * */
    protected void processData(String json) {
        Gson gson = new Gson();
        //将json字段转化为UpdateInfo类泛型
        updateInfo = gson.fromJson(json, HostsUpdateInfo.class);
        Log.i("json解析结果", updateInfo.toString());
        //将解析更新内容保存到数据库并显示出来
        showHostStateInfo();
    }

    private void showHostStateInfo() {
        boolean haveHosts = SPUtils.getBoolean(getApplicationContext(), ConstantValues.HAVE_HOSTS, false);
        Log.i("tag", "是否含有hosts:" + haveHosts);
        if (haveHosts) {
            //设置hosts状态字体为绿色
            mHostsState.setTextColor(Color.GREEN);
            mHostsState.setText("hosts状态：已安装" + SPUtils.getString(getApplicationContext(), ConstantValues.UPDATED_DATE, ""));
            if (updateInfo != null) {
                mUpdateContent.setText("最新hosts：" + updateInfo.date + "\n" + updateInfo.content);
            }
        } else {
            //设置hosts状态字体为黄色
            mHostsState.setTextColor(Color.parseColor("#e4e430"));
            mHostsState.setText("hosts状态：未安装");
            if (updateInfo != null) {
                mUpdateContent.setText("最新hosts：" + updateInfo.date + "\n" + updateInfo.content);
            }
        }
        if (updateInfo != null) {
            if (SPUtils.getString(getApplicationContext(), ConstantValues.UPDATED_DATE, "").equals(updateInfo.date)) {
                mUpdateButton.setBackgroundResource(R.color.colorPrimary);
            } else {
                mUpdateButton.setBackgroundResource(R.color.colorYellow);
            }
        }
    }

    public void getUpdateInfoFromServer() {

        RequestParams params = new RequestParams(ConstantValues.URL_GET_NEW_APP_MESSAGE);

        //发送request请求
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //请求成功
                Log.d("请求服务器的结果", result);
                //解析数据，获取更新对象
                processAppUpdateInfo(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //请求失败
                ex.printStackTrace();
                Log.i("请求失败的息：", ex.toString());
                //提示失败信息
                Toast.makeText(getApplicationContext(), "服务器连接失败\n请检查您的网络连接", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processAppUpdateInfo(String result) {
        Gson gson = new Gson();
        AppUpdateInfo appUpdateInfo = gson.fromJson(result, AppUpdateInfo.class);
        Log.i("tag", "解析到的app更新信息如下：" + appUpdateInfo.toString());

        //如果版本号不一致，则开始更新
        if (!getVersionName().equals(appUpdateInfo.versionName)) {
            //版本不一样，提示更新
            showUpdateAppDialog(appUpdateInfo);
        }
    }

    /**
     * 提示更新app版本
     *
     * @param appUpdateInfo
     */
    private void showUpdateAppDialog(final AppUpdateInfo appUpdateInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setTitle("服务器有新版本了！");
        alertDialog.setMessage("版本号：" + appUpdateInfo.versionName + "\n更新内容：" + appUpdateInfo.updateInfo);
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载新版本
                downloadNewAppFile(appUpdateInfo.updateUrl, appUpdateInfo.versionName);
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //初始化状态文本的默认状态
                initTextHintShow();
                //设置进度条不可见
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /**
     * 下载新版apk文件
     *
     * @param updateUrl 下载地址
     */
    private void downloadNewAppFile(String updateUrl, String versionName) {
        //初始化进度条
        initProgressDialog();

        //设置请求参数
        RequestParams params = new RequestParams(updateUrl);
        params.setAutoResume(true);//设置是否在下载是自动断点续传
        params.setAutoRename(false);//设置是否根据头信息自动命名文件
        params.setSaveFilePath(ConstantValues.SAVE_FILE_PATH);
        params.setExecutor(new PriorityExecutor(2, true));//自定义线程池,有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
        params.setCancelFast(true);//是否可以被立即停止.
        //下面的回调都是在主线程中运行的,这里设置的带进度的回调
        cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onCancelled(CancelledException arg0) {
                Log.i("tag", "取消" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Log.i("tag", "onError: 失败" + Thread.currentThread().getName());
                progressDialog.dismiss();
            }

            @Override
            public void onFinished() {
                Log.i("tag", "完成,每次取消下载也会执行该方法" + Thread.currentThread().getName());
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(File arg0) {
                Log.i("tag", "下载成功的时候执行" + Thread.currentThread().getName());
                //进行apk安装
                File file = new File(ConstantValues.SAVE_FILE_PATH);
                if (file.exists()) {
                    //进入应用安装界面
                    String fileName = file.getAbsolutePath();
                    Uri uri = Uri.fromFile(new File(fileName));
                    //开启intent
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //设置安装数据和安装类型
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if (isDownloading) {
                    progressDialog.setProgress((int) (current * 100 / total));
                    Log.i("tag", "下载中,会不断的进行回调:" + Thread.currentThread().getName());
                }
            }

            @Override
            public void onStarted() {
                Log.i("tag", "开始下载的时候执行" + Thread.currentThread().getName());
                progressDialog.show();
            }

            @Override
            public void onWaiting() {
                Log.i("tag", "等待,在onStarted方法之前执行" + Thread.currentThread().getName());
            }

        });
    }

    /*初始化对话框*/
    private void initProgressDialog() {
        //创建进度条对话框
        progressDialog = new ProgressDialog(this);
        //设置标题
        progressDialog.setTitle("下载文件");
        //设置信息
        progressDialog.setMessage("下载中...");
        progressDialog.setCancelable(false);
        //设置显示的格式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //设置按钮
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击取消正在下载的操作
                cancelable.cancel();
            }
        });
    }


    public String getVersionName() {
        //1.获取包管理对象
        PackageManager packageManager = getPackageManager();
        //2.获取版本信息，参数0代表获取基本信息
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //返回版本名称
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
