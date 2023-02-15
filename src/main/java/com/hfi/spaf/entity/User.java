package com.hfi.spaf.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (User)实体类
 *
 * @author txjava
 * @since 2023-02-15 15:57:15
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 471248315870812347L;
    public static final String ID = "id";
    public static final String USER_NAME = "user_name";
    public static final String COMPANY_NAME = "company_name";
    public static final String DEPARTMENT_NAME = "department_name";
    public static final String PHONE = "phone";
    public static final String MAILBOX = "mailbox";
    public static final String NOTES = "notes";

    private int id;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String mailbox;
    /**
     * 留言
     */
    private String notes;
    /**
     * 创建时间
     */
    private Date createTime;

}

