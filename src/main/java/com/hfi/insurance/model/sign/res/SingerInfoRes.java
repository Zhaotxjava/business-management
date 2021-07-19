package com.hfi.insurance.model.sign.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/19 20:03
 * @Description:
 */
@Data
public class SingerInfoRes {

    private String accountName;

    @ApiModelProperty("用户类型;1:内部,2外部")
    private String accountType;

    private String accountId;

    private String signStatus;
}
