package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/1 18:48
 * @Description:
 */
@ApiModel("签署人信息")
@Data
public class StandardSignerInfoBean {
    private String accountId;
    private Integer accountType;
    private Integer allowApiSign;
    private String authorizationOrganizeId;
    private String authorizationOrganizeNo;
    private Boolean autoSign;
    private String contactMobile;
    private Integer isOrsign;
    private Integer legalSignFlag;
    private List<StandardSignDocBean> signDocDetails;
    private Integer signOrder;
    private String uniqueId;
}
