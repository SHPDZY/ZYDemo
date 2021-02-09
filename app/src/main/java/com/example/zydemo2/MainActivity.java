package com.example.zydemo2;

import android.Manifest;
import android.view.View;
import android.widget.Button;

import com.example.zydemo2.checkpermissions.CheckPermissions;
import com.example.zydemo2.checkpermissions.CheckPermissionsDenied;
import com.example.zydemo2.injectview.ContentView;
import com.example.zydemo2.injectview.InjectView;

/**
 * @author 18964
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    @InjectView(id = R.id.button1 , hasClick = true)
    private Button button1;
    @InjectView(id = R.id.button2 , hasClick = true)
    private Button button2;
    @InjectView(id = R.id.button3 , hasClick = true)
    private Button button3;

    @Override
    void initView() {
        if (button1 != null) {
            button1.setText("自动初始化成功1");
        }
        if (button2 != null) {
            button2.setText("自动初始化成功2");
        }
        if (button3 != null) {
            button3.setText("自动初始化成功3");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.button1:
                checkPermission();
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

    @CheckPermissions({
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    })
    private void checkPermission() {
        toast("点击button1");
    }

    @CheckPermissionsDenied()
    private void checkPermissionsDenied() {
        toast("权限获取失败");
    }
}