package com.example.zydemo2.injectview;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

    @Pointcut("execution(* com.example.zydemo2.BaseActivity.initView(..))")
    public void onInjectView() {

    }

    @Around("onInjectView()")
    public void handleInjectView(ProceedingJoinPoint joinPoint) {
        traversalsField(joinPoint);
    }


    /**
     * 遍历类变量，获取变量注解
     *
     * @param joinPoint
     */
    private void traversalsField(ProceedingJoinPoint joinPoint) {
        try {
            Activity activity = null;
            Log.e(TAG, "traversalsField " + joinPoint.toShortString());
            //获取类所有属性，包括public，private，protected
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
            if (!aClass.isAnnotationPresent(ContentView.class)) {
                Log.e(TAG, "未设置ContentView");
                Toast.makeText(activity, "未设置ContentView", Toast.LENGTH_SHORT).show();
                return;
            }
            ContentView contentView = aClass.getAnnotation(ContentView.class);
            int layoutRsId = contentView.value();
            Method setContentView = aClass.getMethod("setContentView", int.class);
            setContentView.invoke(activity, layoutRsId);
            Field[] fields = aClass.getDeclaredFields();
            if (fields.length > 0) {
                for (Field field : fields) {
                    //判断属性注解是否属于自定义注解接口
                    if (field.isAnnotationPresent(InjectView.class)) {
                        //获取变量注解类型
                        InjectView injectView = field.getAnnotation(InjectView.class);
                        //得到设置的ID
                        int id = injectView.id();
                        //如果获取的ID不等于默认ID，则通过findViewById来查找出对象然后设置变量值
                        if (id != InjectView.DEFAULT_ID) {
                            //类中的成员变量为private,故必须进行此操作
                            field.setAccessible(true);
                            field.set(activity, activity.findViewById(id));
                        }
                        //将属性转成Object类型
                        View view = null;
                        //判断Object类型是否是view的实例，如果是强转成view并设置点击事件
                        if (field.get(activity) instanceof View) {
                            view = (View) field.get(activity);
                        }
                        if (view != null) {
                            boolean clickable = injectView.clickable();
                            view.setClickable(clickable);
                            boolean hasClick = injectView.hasClick();
                            if (hasClick && clickable) {
                                //得到设置方法名
                                String method = injectView.click();
                                if (!method.equals(InjectView.DEFAULT_METHOD)) {
                                    setViewClickListener(view, activity, method);
                                } else if (activity instanceof View.OnClickListener) {
                                    setViewClickListener(view, activity);
                                }
                            }
                        }
                    }
                }
            }
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 给View设置点击事件
     *
     * @param view
     * @param injectedSource 类对象
     */
    private void setViewClickListener(View view, Object injectedSource) {
        view.setOnClickListener((View.OnClickListener) injectedSource);
    }

    /**
     * 给View设置点击事件
     *
     * @param injectedSource 类对象
     * @param method         方法名
     */
    private void setViewClickListener(View view, Activity injectedSource, String method) {
        view.setOnClickListener(new EventListener(injectedSource).click(method));
    }

    static class EventListener implements View.OnClickListener {

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
