package com.hfi.insurance.enums;

/**
 * @Author ChenZX
 * @Date 2021/7/14 17:28
 * @Description:
 */
public enum  ETemplateType {
    FILE_UPLOAD(1, "文件直传"),

    TEMPLATE_FILL(2, "模板填充");

    private int code;

    private String text;

    ETemplateType(int code, String text) {
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
