package cn.jkdev.host.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import cn.jkdev.host.R;

/**
 * Created by pan on 17-8-31.
 */

public class AboutActivity extends AppCompatActivity {

    private TextView mAppName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();

        initUI();

        initData();
    }

    private void initView() {
        mAppName = (TextView) findViewById(R.id.tv_app_name);
    }

    private void initUI() {
        //设置对toolBar的支持
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        //设置默认按钮的支持
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        //显示app版本名称
        mAppName.setText("host科学上网" + getVersionName());
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
