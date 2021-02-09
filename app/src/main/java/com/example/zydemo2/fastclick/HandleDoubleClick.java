package com.example.zydemo2.fastclick;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 18964
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface HandleDoubleClick {

    /**
     * 拦截所有两次点击时间间隔小于一秒的点击事件
     */
    int value() default 1000;

}
