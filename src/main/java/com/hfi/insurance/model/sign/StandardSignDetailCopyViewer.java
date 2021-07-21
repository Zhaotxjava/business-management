package com.hfi.insurance.model.sign;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/21 19:48
 * @Description:
 */
@Data
public class StandardSignDetailCopyViewer {
    private String accountId;// 抄送人accountId
    private String accountName;// 抄送人姓名
    private String uniqueId; //抄送人uniqueId string
}
