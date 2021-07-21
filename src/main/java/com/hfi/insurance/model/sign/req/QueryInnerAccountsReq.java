package com.hfi.insurance.model.sign.req;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/21 9:40
 * @Description:
 */
@Data
public class QueryInnerAccountsReq {

    private String name;

    private String licenseNumber;

    private String uniqueId;

    private Integer licenseType;

    private String organizeId;

    private Integer level;

    private String accountId;

    private String pageIndex;

    private String pageSize;
}
