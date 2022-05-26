package com.hfi.insurance.model.sign;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/8/9 17:27
 * @Description:
 */
@Data
public class SealUser {

    private String accountId;

    private String accountName;

    @JSONField(name = "default")
    private boolean defaults;

}
