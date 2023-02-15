package com.hfi.spaf.dto.system.request;

import com.hfi.spaf.dto.common.PageDto;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class UserDto  extends PageDto {

    /**
     * 用户姓名
     */
    @NotBlank(message = "用户姓名不能为空", groups = {ct.class})
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
    @NotBlank(message = "手机号不能为空", groups = {ct.class})
    private String phone;
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空", groups = {ct.class})
    private String mailbox;
    /**
     * 留言
     */
    private String notes;
    /**
     * 创建时间
     */
    private Date createTime;

    public interface ct {

    }


}
