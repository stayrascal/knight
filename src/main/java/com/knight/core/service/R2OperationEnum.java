package com.knight.core.service;

/**
 * Date: 2015/11/28
 * Time: 22:12
 *
 * @author Rascal
 */
public enum R2OperationEnum {

    add("添加关联"),

    delete("删除关联"),

    update("更新关联");

    private String label;

    private R2OperationEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
