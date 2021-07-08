package com.hfi.insurance.model.sign.req;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/8 15:31
 * @Description:
 */
@Data
public class TemplateQrContentParam {
    private String qrContent;
    private Integer qrId;
}
