package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty("用户id")
    private String accountId;
    @ApiModelProperty("用户类型;1:内部,2外部")
    private Integer accountType;
    @ApiModelProperty("用户名")
    private String accountName;
    @ApiModelProperty("机构编码")
    private String organizeNo;
    @ApiModelProperty("机构名")
    private String organizeName;
    private Integer allowApiSign;
    private String authorizationOrganizeId;
    private String authorizationOrganizeNo;
    @ApiModelProperty("是否静默签署")
    private Boolean autoSign;
    private String contactMobile;
    private Integer isOrsign;
    private Integer legalSignFlag;
    private List<StandardSignDocBean> signDocDetails;
    private Integer signOrder;
    private String uniqueId;
}
