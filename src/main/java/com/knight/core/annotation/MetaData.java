package com.knight.core.annotation;

import java.lang.annotation.*;

/**
 * 用于注解类或属性的元数据，这些元数据可用于代码生成或者运行时动态内容生成
 * @author Rascal
 * Date： 2015/11/20
 * Time： 10:37
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PACKAGE})
public @interface MetaData {

    /**
     * 简要注解说明：一般对应表单项Label属性显示
     */
    String value();

    /**
     * 提示信息： 一般对应表单项的提示说明，支持HTML格式
     */
    String tooltips() default "";

    /**
     * 注释说明：用于描述代码内部用法说明，一般不用于前段UI显示
     */
    String comments() default "";

    /**
     * 标识属性是否出现在版本比较列表
     */
    boolean comparable() default true;

    /**
     * 标识属性是否在代码生成项可编辑
     */
    boolean editable() default true;

    /**
     * 对于自增类型实体设置AUTO_INCREMENT初始值
     * 一般用于业务对象如订单直接用ID作为订单号，希望编号能对齐，则直接把自增初始值为较长位数数字。如100000000
     */
    long autoIncrementInitValue() default 0;
}
