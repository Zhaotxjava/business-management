package com.hfi.insurance.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jthealth-NZH
 * @Date 2021/9/10 14:16
 * @Describe
 * @Version 1.0
 */
public enum PicType {
    /**
     *
     */
    XKZ("xkz", "许可证"),

    YYZZ("yyzz", "营业执照"),

    UNKNOW("","未知类型");
    ;

    private String code;

    private String text;

    PicType(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static PicType getPicType(String type){
        if (StringUtils.isBlank(type)){
            return UNKNOW;
        }
        for (PicType p:PicType.values()
             ) {
            if(p.getCode().equals(type)){
                return p;
            }
        }
        return UNKNOW;
    }
}
