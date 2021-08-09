package com.hfi.insurance.model.sign;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/8/9 14:04
 * @Description:
 */
@Data
public class Seal {
    private String sealId;
    private Integer subSealTypeId;
    private String sealStructType;
    private String downloadUrl;
    private Integer sealType;
    private Integer width;
    private String alias;
    private String subType;
    private Integer sealWay;
    private Integer height;
    private Long createDate;
    private Integer status;
}
