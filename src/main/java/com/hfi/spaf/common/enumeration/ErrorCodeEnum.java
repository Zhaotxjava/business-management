package com.hfi.spaf.common.enumeration;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口返回统一异常情况
 */
public enum ErrorCodeEnum {
    SUCCESS("200", "success"),
    NOT_FOUND_SUCCESS("404", "找不到"),
    SYSTEM_ERROR("500", "系统错误"),
    PARAM_ERROR("50901", "参数错误"),
    STATS_REVERSE_LIST("5156","无符合条件");

    /**
     * 枚举值
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String message;

    /**
     * 构造方法
     *
     * @param code
     * @param message
     */
    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }



    /**
     * 通过枚举<code>code</code>获得枚举
     * <p>
     * values() 方法将枚举转变为数组
     *
     * @return AuthGradeEnum
     */
    public static ErrorCodeEnum getByCode(String code) {
        for (ErrorCodeEnum enumList : values()) {
            if (enumList.getCode().equals(code)) {
                return enumList;
            }
        }
        return null;
    }

    /**
     * 获取全部枚举
     *
     * @return List<AuthGradeEnum>
     */
    public static List<ErrorCodeEnum> getAllEnum() {
        List<ErrorCodeEnum> list = new ArrayList<>(values().length);
        for (ErrorCodeEnum enumList : values()) {
            list.add(enumList);
        }
        return list;
    }

    /**
     * 获取全部枚举值
     *
     * @return List<String>
     */
    public static List<String> getAllEnumCode() {
        List<String> list = new ArrayList<>(values().length);
        for (ErrorCodeEnum enumList : values()) {
            list.add(enumList.getCode());
        }
        return list;
    }

    public static boolean isInEnum(String code) {
        for (ErrorCodeEnum enumList : values()) {
            if (enumList.getCode() == code) {
                return true;
            }
        }
        return false;
    }

}
