package com.example.zydemo2;

import android.app.Application;

import com.example.zydemo2.utils.PermissionsUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PermissionsUtil.getInstance().build(new PermissionsUtil.Builder()
                .setDialogTitle("权限已被禁用，是否手动设置权限？")
                .setDialogCancel("取消")
                .setDialogSure("去设置"));
    }
}
