package com.example.zydemo2.fastclick;

import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 定义切面
 */
@Aspect
public class TraceAspect {

    @Pointcut("execution(* com.example.zydemo2.BaseActivity.on*(..))")
    public void onLifecycleLog() {

    }

    @Before("onLifecycleLog()")
    public void handleLifecycleLog(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        Log.e(TAG, name + "----->>>>>" + joinPoint);
    }

    private final String TAG = this.getClass().getSimpleName();
    /**
     * 上次点击的时间
     */
    private static Long sLastclick = 0L;
    /**
     * 拦截所有两次点击时间间隔小于一秒的点击事件
     */
    private static final Long FILTER_TIMEM = 1000L;
    /**
     * 上次点击事件View
     */
    @IdRes
    private int mLastViewId;

    @Pointcut("execution(@ com.example.zydemo2.fastclick.HandleDoubleClick * *(..))")
    public void onHandleDoubleClick() {

    }

    @Around("onHandleDoubleClick()")
    public void handleDoubleClick(ProceedingJoinPoint joinPoint) {
        if (System.currentTimeMillis() - sLastclick >= FILTER_TIMEM) {
            doClick(joinPoint);
        } else {
            if (mLastViewId != ((View)joinPoint.getArgs()[0]).getId()) {
                doClick(joinPoint);
            } else {
                //大于指定秒数 且是同一个view
                Log.e(TAG, "重复点击,已过滤id:"+ ((View)joinPoint.getArgs()[0]).getId());
            }
        }

    }

    private void doClick(ProceedingJoinPoint joinPoint) {
        try {
            if (joinPoint.getArgs().length == 0) {
                joinPoint.proceed();
                return;
            }
            mLastViewId = ((View) joinPoint.getArgs()[0]).getId();
            //记录点击事件
            sLastclick = System.currentTimeMillis();
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Log.e(TAG, "过滤重复点击出错：" + throwable.getMessage());
        }
    }
}
