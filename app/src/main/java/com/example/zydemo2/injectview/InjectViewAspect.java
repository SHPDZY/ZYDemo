package com.example.zydemo2.injectview;

import android.util.Log;
import android.view.View;

import com.example.zydemo2.BaseActivity;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * AOP自动插入初始化VIEW代码
 */
@Aspect
public class InjectViewAspect {

    private final String TAG = this.getClass().getSimpleName();

    @Pointcut("execution(* com.example.zydemo2.injectview.InjectViewUtils.init(..))")
    public void onInjectView() {

    }

    @Around("onInjectView()")
    public void handleInjectView(ProceedingJoinPoint joinPoint) {
        traversalsField(joinPoint);
    }


    /**
     * 遍历类变量，获取变量注解
     * @param joinPoint
     */
    private void traversalsField(ProceedingJoinPoint joinPoint) {
        Log.e(TAG,"traversalsField "+joinPoint.toShortString());
        //获取类所有属性，包括public，private，protected
        BaseActivity activity = (BaseActivity) joinPoint.getArgs()[0];
        Field[] fields = activity.getClass().getDeclaredFields();
        if (null != fields && fields.length > 0) {
            for (Field field : fields) {
                //判断属性注解是否属于自定义注解接口
                if (field.isAnnotationPresent(InjectView.class)) {
                    //获取变量注解类型
                    InjectView injectView = field.getAnnotation(InjectView.class);
                    //得到设置的ID
                    int id = injectView.id();
                    //如果获取的ID不等于默认ID，则通过findViewById来查找出对象然后设置变量值
                    if (id != InjectView.DEFAULT_ID) {
                        try {
                            //类中的成员变量为private,故必须进行此操作
                            field.setAccessible(true);
                            field.set(activity, activity.findViewById(id));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    //得到设置方法名
                    String method = injectView.click();
                    if (!method.equals(InjectView.DEFAULT_METHOD)) {
                        setViewClickListener(activity, field, method);
                    }
                }
            }
        }
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 给View设置点击事件
     *
     * @param injectedSource 类对象
     * @param field          属性
     * @param clickMethod    方法名
     */
    private void setViewClickListener(Object injectedSource, Field field, String clickMethod) {
        try {
            //将属性转成Object类型
            Object obj = field.get(injectedSource);
            //判断Object类型是否是view的实例，如果是强转成view并设置点击事件
            if (obj instanceof View) {
                ((View) obj).setOnClickListener(new EventListener(injectedSource).click(clickMethod));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class EventListener implements View.OnClickListener {

        /**
         * 类对象
         */
        public Object obj;
        /**
         * 方法名
         */
        public String clickMethod;

        public EventListener(Object obj) {
            this.obj = obj;
        }

        /**
         * click返回的是实现了OnClickListener接口的实例
         */
        public EventListener click(String clickMethod) {
            this.clickMethod = clickMethod;
            return this;
        }

        //当view点击时会调用onClick方法
        @Override
        public void onClick(View v) {
            invokeClickMethod(obj, clickMethod, v);
        }

        private Object invokeClickMethod(Object obj, String methodName, Object... params) {
            if (obj == null) {
                return null;
            }
            Method method = null;
            try {
                //获取类对象中以methodName和接受一个View参数的类型方法
                method = obj.getClass().getDeclaredMethod(methodName, View.class);
                if (method != null) {
                    //类中的方法为private,故必须进行此操作
                    method.setAccessible(true);
                    //执行方法，并传递当前对象
                    return method.invoke(obj, params);
                } else {
                    throw new Exception("no such method:" + methodName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
