package com.hfi.insurance.enums;

/**
 * @author jthealth-NZH
 * @Date 2022/4/13 10:30
 * @Describe
 * @Version 1.0
 */
public enum BatchQueryTypeEnum {
    BLANK,SINGLE_NUMBER,SINGLE_NAME,NUMBERS,NAMES;

    public static BatchQueryTypeEnum getType(String str){
        switch (str){
            case "SINGLE_NUMBER":
                return SINGLE_NUMBER;
            case "SINGLE_NAME":
                return SINGLE_NAME;
            case "NUMBERS":
                return NUMBERS;
            case "NAMES":
                return NAMES;
            default:
                return BLANK;
        }

    }
}
