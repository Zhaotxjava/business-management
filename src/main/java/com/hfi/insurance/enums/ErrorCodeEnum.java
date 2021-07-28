package com.hfi.insurance.enums;

import java.util.ArrayList;
import java.util.List;

public enum ErrorCodeEnum {
    SUCCESS("200", "success"),
    SYSTEM_ERROR("500", "系统错误"),
    PARAM_ERROR("50901", "参数错误"),
    NETWORK_ERROR("50904", "对方服务器连接失败"),
    DATA_NOT_READY_ERROR("50905", "数据还未准备结束"),
    RESPONES_ERROR("50906", "接口响应错误"),
    TOKEN_EXPIRED("50907", "token已经过期，请重新登录！！");

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
     * 通过code获取msg
     *
     * @param code 枚举值
     * @return
     */
    public static String getMsgByCode(String code) {
        if (code == null) {
            return null;
        }
        ErrorCodeEnum enumList = getByCode(code);
        if (enumList == null) {
            return null;
        }
        return enumList.getMessage();
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


   /* public static void main(String[] args) {
        System.out.println(isInEnum(99));
    }*/
}
