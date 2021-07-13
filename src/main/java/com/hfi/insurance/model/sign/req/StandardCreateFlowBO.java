package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/1 18:22
 * @Description:
 */
@Data
public class StandardCreateFlowBO {

    @ApiModelProperty("是否允许签署人补充签署 材料0否1是 默认：0")
    private Integer allowAddAttachment;

    private ProcessAppInfoBean appInfo;
    private List<AttachmentProcessBean> attachments;
    @ApiModelProperty("第三方业务码")
    private String bizNo;

    @ApiModelProperty("签署回调通知地址")
    private String callbackUrl;

    @ApiModelProperty("流程备注")
    private String comments;

    private List<CopyViewerInfoBean> copyViewers;

    @ApiModelProperty("发起人用户id")
    private String initiatorAccountId;

    @ApiModelProperty("发起人用户类型，1-内 部，2-外部，非必填")
    private Integer initiatorAccountType;

    @ApiModelProperty("发起人邮箱")
    private String initiatorEmail;

    @ApiModelProperty("发起人手机号")
    private String initiatorMobile;

    @ApiModelProperty("发起人姓名")
    private String initiatorName;

    @ApiModelProperty("发起人所属部门id")
    private String initiatorOrganizeNo;

    @ApiModelProperty("发起人用户唯一标识")
    private String initiatorUniqueId;

    @ApiModelProperty("是否为手机盾签署，默认false")
    private Boolean mobileShieldSignFlag;

    @ApiModelProperty("文档二维码相关参数")
    private QrDocParamBean qrDoc;

    @ApiModelProperty("是否开启文档二维码，默认false")
    private Boolean qrDocSwitcher;

    @ApiModelProperty("签署完成重定向地址")
    private String redirectUrl;

    @ApiModelProperty(value = "流程文档信息集合",required = true)
    private List<FlowDocBean> signDocs;

    @ApiModelProperty("签署文件类型 PDF/OFD")
    private String signFileFormat;

    @ApiModelProperty("签署场景编号")
    private String signSceneNo;

    @ApiModelProperty("签署有效期")
    private String signValidity;

    @ApiModelProperty(value = "签署人信息集合",required = true)
    private List<StandardSignerInfoBean> signers;

    @ApiModelProperty(value = "流程主题",required = true)
    private String subject;

    @ApiModelProperty("是否Ukey签署")
    private Integer ukeySignFlag;
}
