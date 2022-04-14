package com.hfi.insurance.enums;

/**
 * @author jthealth-NZH
 * @Date 2022/4/13 10:30
 * @Describe
 * @Version 1.0
 */
public enum BatchQueryTypeEnum {
    BLANK,SIGNLE_NUMBER,SIGNLE_NAME,NUMBERS,NAMES;

    public static BatchQueryTypeEnum getType(String str){
        switch (str){
            case "SIGNLE_NUMBER":
                return SIGNLE_NUMBER;
            case "SIGNLE_NAME":
                return SIGNLE_NAME;
            case "NUMBERS":
                return NUMBERS;
            case "NAMES":
                return NAMES;
            default:
                return BLANK;
        }

    }
}
