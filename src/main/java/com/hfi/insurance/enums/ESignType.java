package com.hfi.insurance.enums;

/**
 * @Author ChenZX
 * @Date 2021/7/13 10:55
 * @Description:
 */
public enum ESignType {

//    UNLIMITED(0,"不限"),
//
//    SINGLE_PAGE_SIGN(1, "单页签"),
//
//    MULTI_PAGE_SIGN(2, "多页签"),
//
//    SEAL_ON_THE_PERFORATION(3, "骑缝章"),
//
//    KEY_WORD_SIGN(4, "关键字签"),
//
//    POSITION_SIGN(5, "位置签");
    DEFAULT_COORDINATE_SIGN(0,"静默坐标签署"),
    DEFAULT_KEY_WORD_SIGN(1,"静默关键字签署"),
    MANUAL_FREEDOM_SIGN(2,"手动自由签署"),
    MANUAL_COORDINATE_SIGN(3,"手动坐标签署"),
    MANUAL_KEY_WORD_SIGN(4,"手动关键字签署");

    private int code;

    private String text;

    ESignType(int code, String text) {
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
