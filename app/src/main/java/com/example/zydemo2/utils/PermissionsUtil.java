package com.example.zydemo2.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.zydemo2.App;

import java.util.ArrayList;
import java.util.List;

public class PermissionsUtil {

    private Activity mContent;
    //权限请求码
    private final int mRequestCode = 1001;
    private boolean showSystemSetting = true;
    private static PermissionsUtil permissionsUtil;
    private IPermissionsResult mPermissionsResult;
    private AlertDialog mPermissionDialog;
    private String mDialogTitle = "权限已被禁用，是否手动设置权限？";
    private String mDialogCancel = "取消";
    private String mDialogSure = "去设置";

    private PermissionsUtil() {
    }

    public static PermissionsUtil getInstance() {
        if (permissionsUtil == null) {
            synchronized (PermissionsUtil.class) {
                if (permissionsUtil == null) {
                    permissionsUtil = new PermissionsUtil();
                }
            }
        }
        return permissionsUtil;
    }

    public void init(Activity context) {
        this.mContent = context;
    }

    public void build(Builder builder) {
        showSystemSetting = builder.showDialog;
        if (!TextUtils.isEmpty(builder.mDialogCancel)) {
            mDialogCancel = builder.mDialogCancel;
        }
        if (!TextUtils.isEmpty(builder.mDialogSure)) {
            mDialogSure = builder.mDialogSure;
        }
        if (!TextUtils.isEmpty(builder.mDialogTitle)) {
            mDialogTitle = builder.mDialogTitle;
        }
    }

    public void checkPermissions(String[] permissions, @NonNull IPermissionsResult permissionsResult) {
        mPermissionsResult = permissionsResult;
        if (Build.VERSION.SDK_INT < 23) {
            //6.0才用动态权限
            permissionsResult.onSuccess();
            return;
        }
        //创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
        List<String> mPermissionList = new ArrayList<>();
        //逐个判断你要的权限是否已经通过
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContent, permission) != PackageManager.PERMISSION_GRANTED) {
                //添加还未授予的权限
                mPermissionList.add(permission);
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {
            //有权限没有通过，需要申请
            ActivityCompat.requestPermissions(mContent, permissions, mRequestCode);
        } else {
            //说明权限都已经通过，可以做你想做的事情去
            permissionsResult.onSuccess();
        }
    }

    /**
     * 请求权限后回调的方法
     *
     * @param requestCode  是我们自己定义的权限请求码
     * @param permissions  是我们请求的权限名称数组
     * @param grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //有权限没有通过
        boolean hasPermissionDismiss = false;
        if (mRequestCode == requestCode) {
            for (int grantResult : grantResults) {
                if (grantResult == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                if (showSystemSetting) {
                    //跳转到系统设置权限页面，或者直接关闭页面，不让继续访问
                    showSystemPermissionsSettingDialog();
                } else {
                    mPermissionsResult.onFail();
                }
            } else {
                //全部权限通过，可以进行下一步操作。。。
                mPermissionsResult.onSuccess();
            }
        }
    }

    /**
     * 不再提示权限时的展示对话框
     */
    private void showSystemPermissionsSettingDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(mContent)
                    .setCancelable(false)
                    .setMessage(mDialogTitle)
                    .setPositiveButton(mDialogSure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            goToSetting();
                        }
                    })
                    .setNegativeButton(mDialogCancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            cancelPermissionDialog();
                            mPermissionsResult.onFail();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    /**
     * 去设置页面
     */
    private void goToSetting() {
        final String mPackName = mContent.getPackageName();
        Uri packageURI = Uri.parse("package:" + mPackName);
        mContent.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI));
        mContent.onBackPressed();
    }

    //关闭对话框
    private void cancelPermissionDialog() {
        if (mPermissionDialog != null) {
            mPermissionDialog.cancel();
            mPermissionDialog = null;
        }
    }

    public interface IPermissionsResult {
        void onSuccess();

        void onFail();
    }

    public static final class Builder {

        private boolean showDialog = true;
        private String mDialogTitle;
        private String mDialogCancel;
        private String mDialogSure;

        public boolean isShowDialog() {
            return showDialog;
        }

        public Builder setShowDialog(boolean showDialog) {
            this.showDialog = showDialog;
            return this;
        }

        public String getDialogTitle() {
            return mDialogTitle;
        }

        public Builder setDialogTitle(String mDialogTitle) {
            this.mDialogTitle = mDialogTitle;
            return this;
        }

        public String getDialogCancel() {
            return mDialogCancel;
        }

        public Builder setDialogCancel(String mDialogCancel) {
            this.mDialogCancel = mDialogCancel;
            return this;
        }

        public String getDialogSure() {
            return mDialogSure;
        }

        public Builder setDialogSure(String mDialogSure) {
            this.mDialogSure = mDialogSure;
            return this;
        }
    }
}