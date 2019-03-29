package cn.jkdev.host.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jkdev.host.R;
import cn.jkdev.host.bean.FeedbackInfo;


/**
 * Created by pan on 17-8-31.
 */

public class FeedBackActivity extends AppCompatActivity {
    private String TAG = "FeedbackActivity";
    private TextInputLayout mContent;
    private TextInputLayout mAddress;

    //FireBase
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initUI();

        initData();
    }

    private void initData() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("feedback");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return super.onCreateOptionsMenu(menu);
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

        //获取数据
        mContent = (TextInputLayout) findViewById(R.id.til_content);
        mAddress = (TextInputLayout) findViewById(R.id.til_address);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_feedback_submit:
                //弹出提交提示
                showDialogIfSubmit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void submitFeedbackData() {
        //记录当前时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDate = dateFormat.format(new Date());
        //创建数据对象
        FeedbackInfo feedbackInfo = new FeedbackInfo(mContent.getEditText().getText().toString(),
                mAddress.getEditText().getText().toString());
        Log.i(TAG, "开始提交数据");
        //开始提交数据
        Task<Void> voidTask = myRef.child(currentDate).setValue(feedbackInfo);
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "数据提交提交成功");
            }
        });
        //结束当前界面
        finish();
        //提示信息
        Toast.makeText(getApplicationContext(), "您的反馈已经提交\n感谢您的支持", Toast.LENGTH_LONG).show();
    }

    private void showDialogIfSubmit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setTitle("提示！");
        alertDialog.setMessage("您提交的反馈信息将提交到服务器，我们会根据您的反馈对《host科学上网》进行升级和改进，是否继续？");
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //提交数据
                //提交反馈
                submitFeedbackData();
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
}
