package com.hfi.insurance.model.sign;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/8/13 9:46
 * @Description:
 */
@Data
public class FinishDocUrlBean {
    private String docFileKey;// 文档fileKey string
    private Integer docId; //文档id int64
    private String downloadDocUrl; //文档下载地址
}
