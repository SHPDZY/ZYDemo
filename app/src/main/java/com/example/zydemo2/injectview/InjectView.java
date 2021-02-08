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
public @interface InjectView{

    /**
     * 默认控件ID
     */
    public static int DEFAULT_ID = -1;

    /**
     * 默认方法
     */
    public static String DEFAULT_METHOD = "";

    /**
     * 功能:接收控件ID
     * @return 返回设置ID
     */
    public int id() default DEFAULT_ID;

    /**
     * 功能:接收控件点击方法名
     * @return 返回设置方法名
     */
    public String click() default DEFAULT_METHOD;
}
