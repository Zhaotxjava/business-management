package com.hfi.insurance.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ESignType;
import com.hfi.insurance.enums.ETemplateType;
import com.hfi.insurance.mapper.YbFlowInfoMapper;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.YbOrgTd;
import com.hfi.insurance.model.sign.PredefineBean;
import com.hfi.insurance.model.sign.SignatoryBean;
import com.hfi.insurance.model.sign.TemplateFlowBean;
import com.hfi.insurance.model.sign.TemplateInfoBean;
import com.hfi.insurance.model.sign.req.CreateSignFlowReq;
import com.hfi.insurance.model.sign.req.FlowDocBean;
import com.hfi.insurance.model.sign.req.SignInfoBeanV2;
import com.hfi.insurance.model.sign.req.StandardCreateFlowBO;
import com.hfi.insurance.model.sign.req.StandardSignDocBean;
import com.hfi.insurance.model.sign.req.StandardSignerInfoBean;
import com.hfi.insurance.model.sign.req.TemplateFormValueParam;
import com.hfi.insurance.model.sign.req.TemplateUseParam;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.IYbOrgTdService;
import com.hfi.insurance.service.SignedBizService;
import com.hfi.insurance.service.SignedService;
import com.hfi.insurance.utils.EnumHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author ChenZX
 * @Date 2021/7/13 10:40
 * @Description:
 */
@Slf4j
@Service
public class SignedBizServiceImpl implements SignedBizService {

    @Resource
    private SignedService signedService;

    @Resource
    private YbFlowInfoMapper flowInfoMapper;

    @Resource
    private IYbOrgTdService orgTdService;

    @Resource
    private IYbInstitutionInfoService institutionInfoService;

    @Override
    public ApiResponse createSignFlow(CreateSignFlowReq req) {
        StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
        //文档信息
        List<FlowDocBean> signDocs = new ArrayList<>();
        FlowDocBean flowDocBean = new FlowDocBean();
        ETemplateType templateType = EnumHelper.translate(ETemplateType.class, req.getTemplateType());
        String templateId = req.getTemplateId();
        //获取模板信息
        JSONObject templateInfoJson = signedService.getTemplateInfo(templateId);
        String templateStr = templateInfoJson.getString("template");
        TemplateInfoBean templateInfo = JSON.parseObject(templateStr, TemplateInfoBean.class);
        if (ETemplateType.TEMPLATE_FILL == templateType) {
            //填充模板的形式发起签署
            TemplateUseParam templateUseParam = new TemplateUseParam();
            //填充表单信息（文本域）
            List<TemplateFormValueParam> templateFormValues = templateInfo.getTemplateForms().stream().map(templateForm -> {
                TemplateFormValueParam templateFormValueParam = new TemplateFormValueParam();
                templateFormValueParam.setFormId(templateForm.getFormId());
                String formName = templateForm.getFormName();
                Optional<String> institutionNameOptional = req.getInstitutionNameList().stream().filter(formName::equals).findAny();
                institutionNameOptional.ifPresent(templateFormValueParam::setFormValue);
                return templateFormValueParam;
            }).collect(Collectors.toList());
            templateUseParam.setTemplateFormValues(templateFormValues);
            templateUseParam.setTemplateId(templateId);
            JSONObject jsonObject = signedService.buildTemplateDoc(templateUseParam);
            flowDocBean.setDocFilekey(jsonObject.getString("fileKey"));
            flowDocBean.setDocName(templateInfo.getTemplateName());
            signDocs.add(flowDocBean);
            standardCreateFlow.setSignDocs(signDocs);
            //签署人信息
            List<TemplateFlowBean> templateFlows = templateInfo.getTemplateFlows();
            Map<String, PredefineBean> accountSignatoryMap = new HashMap<>();
            Map<String, PredefineBean> flowNamePredefineMap = new HashMap<>();
            templateFlows.forEach(templateFlowBean -> {
                SignatoryBean signatory = templateFlowBean.getSignatory();
                //todo 此处可能会覆盖
                accountSignatoryMap.put(signatory.getAccountId(), templateFlowBean.getPredefine());
                flowNamePredefineMap.put(templateFlowBean.getFlowName(), templateFlowBean.getPredefine());
            });

            List<StandardSignerInfoBean> singerList = req.getSingerInfos().stream().map(singerInfo -> {
                YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(singerInfo.getNumber());
                String accountId = institutionInfo.getAccountId();
                PredefineBean predefineBean = accountSignatoryMap.get(accountId);
                //填充签署人信息
                StandardSignerInfoBean signerInfoBean = new StandardSignerInfoBean();
                signerInfoBean.setAccountId(accountId);
                signerInfoBean.setAccountType(singerInfo.getAccountType());
                ESignType signType = EnumHelper.translate(ESignType.class, singerInfo.getSignType());
                if (ESignType.DEFAULT_COORDINATE_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
                    signerInfoBean.setAutoSign(true);
                }
                //签署文档信息;指定文档进行签署，未指定的文档将作为只读
                List<StandardSignDocBean> signDocDetails = new ArrayList<>();
                StandardSignDocBean standardSignDocBean = new StandardSignDocBean();
                standardSignDocBean.setDocFilekey(templateInfo.getFileKey());
                List<SignInfoBeanV2> signPos = new ArrayList<>();
                SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
                //签署方式默认为0
                signInfoBeanV2.setSignType(0);
                if (ESignType.MANUAL_KEY_WORD_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
                    String keyWord = predefineBean.getKeyWord();
                    signInfoBeanV2.setKey(keyWord != null ? keyWord : singerInfo.getKey());
                }
                //位置签署要匹配区域
                flowNamePredefineMap.keySet().forEach(flowName -> {
                    if ((ESignType.MANUAL_COORDINATE_SIGN == signType || ESignType.DEFAULT_COORDINATE_SIGN == signType)
                            && singerInfo.getFlowName().equals(flowName)) {
                        signInfoBeanV2.setPosX(Float.valueOf(predefineBean.getPosX()));
                        signInfoBeanV2.setPosY(Float.valueOf(predefineBean.getPosY()));
                        signInfoBeanV2.setPosPage(predefineBean.getPageNo());
                    }
                });
                signPos.add(signInfoBeanV2);
                standardSignDocBean.setSignPos(signPos);
                signDocDetails.add(standardSignDocBean);
                signerInfoBean.setSignDocDetails(signDocDetails);
                return signerInfoBean;
            }).collect(Collectors.toList());
            standardCreateFlow.setSigners(singerList);
        } else {
//            文件直传的方式发起签署
            flowDocBean.setDocFilekey(req.getFileKey());
            signDocs.add(flowDocBean);
            standardCreateFlow.setSignDocs(signDocs);
            List<StandardSignerInfoBean> singerList = req.getSingerInfos().stream().map(singerInfo -> {
                //填充签署人信息
                StandardSignerInfoBean signerInfoBean = new StandardSignerInfoBean();
                List<StandardSignDocBean> signDocDetails = new ArrayList<>();
                StandardSignDocBean standardSignDocBean = new StandardSignDocBean();
                standardSignDocBean.setDocFilekey(templateInfo.getFileKey());
                List<SignInfoBeanV2> signPos = new ArrayList<>();
                SignInfoBeanV2 signInfoBean = new SignInfoBeanV2();
                //签署方式默认为0
                signInfoBean.setSignType(0);
                signInfoBean.setKey(singerInfo.getKey());
                signPos.add(signInfoBean);
                standardSignDocBean.setSignPos(signPos);
                signDocDetails.add(standardSignDocBean);
                signerInfoBean.setSignDocDetails(signDocDetails);
                return signerInfoBean;
            }).collect(Collectors.toList());
            standardCreateFlow.setSigners(singerList);
        }
        log.info("创建流程入参：{}", JSON.toJSONString(standardCreateFlow));
        JSONObject signFlows = signedService.createSignFlows(standardCreateFlow);
        String signFlowId = signFlows.getString("signFlowId");
        YbFlowInfo flowInfo = new YbFlowInfo();
        flowInfo.setSignFlowId(signFlowId);
        flowInfoMapper.insert(flowInfo);
        return new ApiResponse(signFlows);
    }
}
