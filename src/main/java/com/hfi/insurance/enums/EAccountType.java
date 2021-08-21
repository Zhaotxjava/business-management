package com.hfi.insurance.enums;

/**
 * @Author ChenZX
 * @Date 2021/8/21 12:59
 * @Description:
 */
public enum  EAccountType {
    INTERNAL(1, "内部"),

    EXTERNAL(2, "外部");

    private int code;

    private String text;

    EAccountType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
