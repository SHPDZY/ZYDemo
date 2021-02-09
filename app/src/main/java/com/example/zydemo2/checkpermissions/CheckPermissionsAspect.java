package com.example.zydemo2.checkpermissions;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.example.zydemo2.injectview.ContentView;
import com.example.zydemo2.utils.PermissionsUtil;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 检查权限AOP
 */
@Aspect
public class CheckPermissionsAspect {

    private final String TAG = this.getClass().getSimpleName();

    @Pointcut("execution(@com.example.zydemo2.checkpermissions.CheckPermissions * *(..)) && @annotation(permission)")
    public void onCheckPermissions(CheckPermissions permission) {

    }

    @Around("onCheckPermissions(permission)")
    public void handleCheckPermissions(final ProceedingJoinPoint joinPoint, CheckPermissions permission) {
        PermissionsUtil.getInstance().checkPermissions(permission.value(),
                new PermissionsUtil.IPermissionsResult() {
                    @Override
                    public void onSuccess() {
                        try {
                            joinPoint.proceed();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        try {
                            handleFail(joinPoint);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void handleFail(ProceedingJoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
        Activity activity = null;
        Object object = joinPoint.getThis();
        if (object instanceof Activity) {
            activity = (Activity) object;
        } else if (object instanceof Fragment) {
            activity = ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            activity = ((android.app.Fragment) object).getActivity();
        }
        if (activity == null) {
            Log.e(TAG, "traversalsField activity is null");
            return;
        }
        Class<? extends Activity> aClass = activity.getClass();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(CheckPermissionsDenied.class)) {
                method.setAccessible(true);
                method.invoke(object);
            }
        }
    }

    @Pointcut("execution(* androidx.fragment.app.FragmentActivity.onRequestPermissionsResult(..))")
    public void onRequestPermissionsResult() {

    }

    @After("onRequestPermissionsResult()")
    public void handleRequestPermissionsResult(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        Log.e(TAG, name + "----->>>>>" + joinPoint);
        Object[] args = joinPoint.getArgs();
        PermissionsUtil.getInstance().onRequestPermissionsResult((int) args[0], (String[]) args[1], (int[]) args[2]);
    }

}
