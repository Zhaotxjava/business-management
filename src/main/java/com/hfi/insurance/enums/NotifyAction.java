package com.hfi.insurance.enums;

import java.util.ArrayList;
import java.util.List;

public enum NotifyAction {
	SIGN_FLOW_FINISH("SIGN_FLOW_FINISH", "终结状态"),
	SIGN_FLOW_UPDATE("SIGN_FLOW_UPDATE", "过程状态"),
	REALNAME_FOR_SIGN("REALNAME_FOR_SIGN", "实名认 证"),
	SIGN_FLOW_NOTIFY("SIGN_FLOW_NOTIFY", "签署通知"),

	;

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
	NotifyAction(String code, String message) {
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
		NotifyAction enumList = getByCode(code);
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
	public static NotifyAction getByCode(String code) {
		for (NotifyAction enumList : values()) {
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
	public static List<NotifyAction> getAllEnum() {
		List<NotifyAction> list = new ArrayList<>(values().length);
		for (NotifyAction enumList : values()) {
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
		for (NotifyAction enumList : values()) {
			list.add(enumList.getCode());
		}
		return list;
	}

	public static boolean isInEnum(String code) {
		for (NotifyAction enumList : values()) {
			if (enumList.getCode() == code) {
				return true;
			}
		}
		return false;
	}

}
