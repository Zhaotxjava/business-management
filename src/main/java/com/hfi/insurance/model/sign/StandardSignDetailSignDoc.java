package com.hfi.insurance.model.sign;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/21 19:57
 * @Description:
 */
@Data
public class StandardSignDetailSignDoc {
    private Integer docId; //签署文件processDocId
    private String docName;// 签署文件名称
    private Integer docOrder;// 签署文件签署顺序
    private String fileKey;// 签署文件Key
    private String note; //签署结果 string
    private Integer printCount;// 打印次数
}
