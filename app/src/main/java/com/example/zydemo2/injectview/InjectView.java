package com.example.zydemo2.injectview;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 18964
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView {

    /**
     * 默认控件ID
     */
    int DEFAULT_ID = -1;

    /**
     * 默认方法
     */
    String DEFAULT_METHOD = "";

    /**
     * 功能:接收控件ID
     *
     * @return 返回设置ID
     */
    int id() default DEFAULT_ID;

    /**
     * 功能:接收控件是否需要点击事件
     *
     * @return 返回是否需要点击事件
     */
    boolean hasClick() default false;

    /**
     * 功能:设置控件是否可点击
     *
     * @return 返回是否可点击
     */
    boolean clickable() default true;

    /**
     * 功能:接收控件点击方法名
     *
     * @return 返回设置方法名
     */
    String click() default DEFAULT_METHOD;
}
