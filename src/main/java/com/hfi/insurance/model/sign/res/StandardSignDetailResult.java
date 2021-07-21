package com.hfi.insurance.model.sign.res;

import com.hfi.insurance.model.sign.StandardSignDetailCopyViewer;
import com.hfi.insurance.model.sign.StandardSignDetailSignDoc;
import com.hfi.insurance.model.sign.StandardSignDetailSigner;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/21 19:46
 * @Description:
 */
@Data
public class StandardSignDetailResult {
    private List<StandardSignDetailCopyViewer> copyViewers; //签署流程抄送人列表
    private Date flowEndTime;// 签署结束时间
    private Date flowStartTime;// 签署开始时间
    // 流程状态:0草稿，1 签署中，2完成，3 撤销，4终止，5过 期，6删除，7拒 签，8作废，9已归 档，10预盖章
    private Integer flowStatus;
    // 冻结状态，0-正常， 1-冻结（未开启作废 流程），2-冻结（已 开始作废流程）
    private Integer forzenStatus;
    private String initiatorAccountId; //发起人accountId
    private String initiatorAccountType;//发起人用户类型，1- 内部，2-外部
    private String initiatorOrganizeId;//发起人部门Id
    private String initiatorOrganizeNo;// 发起人部门编号
    private String initiatorUniqueId;//发起人uniqueId
    private List<StandardSignDetailSignDoc> signDocs;// 签署文件列表
    private String  signFileFormat;// 签署文件类型 PDF/OFD
    private Integer signFlowId; // 流程ID
    private Date signValidity;// 签署有效期
    private  List<StandardSignDetailSigner> signers;// 签署人信息
    private String  subject;// 签署场景
}
