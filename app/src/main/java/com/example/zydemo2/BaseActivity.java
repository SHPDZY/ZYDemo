package com.example.zydemo2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zydemo2.fastclick.HandleDoubleClick;
import com.example.zydemo2.injectview.InjectViewUtils;

/**
 * @author 18964
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(loadContentView());
        InjectViewUtils.init(this);
        initView();
    }

    /**
     * 设置资源文件
     * @return 资源文件
     */
    abstract int loadContentView();

    abstract void initView();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    @HandleDoubleClick()
    public void onClick(View v) {
        log("点击事件触发");
    }

    public void log(String msg){
        Log.e(TAG,msg);
    }

    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}