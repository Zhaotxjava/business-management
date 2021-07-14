package com.hfi.insurance.enums;

/**
 * @Author ChenZX
 * @Date 2021/7/13 10:50
 * @Description:
 */
public enum EYesOrNoType {
    NO(0, "否"),

    YES(1, "是");

    private int code;

    private String text;

    EYesOrNoType(int code, String text) {
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
