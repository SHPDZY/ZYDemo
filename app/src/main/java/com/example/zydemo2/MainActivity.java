package com.example.zydemo2;

import android.view.View;
import android.widget.Button;

import com.example.zydemo2.injectview.InjectView;

public class MainActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    @InjectView(id = R.id.button1, click = "onClick")
    private Button button1;
    @InjectView(id = R.id.button2, click = "onClick")
    private Button button2;
    @InjectView(id = R.id.button3, click = "onClick")
    private Button button3;

    @Override
    int loadContentView() {
        return R.layout.activity_main;
    }

    @Override
    void initView() {
        if (button1!=null){
            button1.setText("自动初始化成功1");
        }
        if (button2!=null){
            button2.setText("自动初始化成功2");
        }
        if (button3!=null){
            button3.setText("自动初始化成功3");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.button1:
                toast("点击button1");
                break;
            case R.id.button2:
                toast("点击button2");
                break;
            case R.id.button3:
                toast("点击button3");
                break;
            default:
        }
    }
}