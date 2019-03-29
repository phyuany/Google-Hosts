package cn.jkdev.host.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import cn.jkdev.host.R;
import cn.jkdev.host.utils.ConstantValues;
import cn.jkdev.host.utils.SPUtils;

/**
 * Created by pan on 17-9-1.
 */

public class SettingActivity extends AppCompatActivity {

    private TextView mTvState;
    private Switch mSwitchState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        intView();

        initData();
    }

    private void initData() {
        //回显数据
        if (SPUtils.getBoolean(getApplicationContext(), ConstantValues.AUTO_UPDATE, true)) {
            mTvState.setText("应用自动检测新版本：已开启");
            mSwitchState.setChecked(true);
        } else {
            mTvState.setText("应用自动检测新版本：已关闭");
            mSwitchState.setChecked(false);
        }
    }

    private void intView() {
        //设置对toolBar的支持
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        //设置默认按钮的支持
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTvState = (TextView) findViewById(R.id.tv_state);
        mSwitchState = (Switch) findViewById(R.id.st_state);

        mSwitchState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //根据按钮的状态保存是否更新的设置
                if (b) {
                    SPUtils.putBoolean(getApplicationContext(), ConstantValues.AUTO_UPDATE, true);
                } else {
                    SPUtils.putBoolean(getApplicationContext(), ConstantValues.AUTO_UPDATE, false);
                }
                //更新数据
                initData();
            }
        });
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

}
