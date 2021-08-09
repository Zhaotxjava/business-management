package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.common.BizServiceException;
import com.hfi.insurance.enums.ESignType;
import com.hfi.insurance.enums.ETemplateType;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.sign.InstitutionBaseInfo;
import com.hfi.insurance.model.sign.Position;
import com.hfi.insurance.model.sign.PredefineBean;
import com.hfi.insurance.model.sign.Seal;
import com.hfi.insurance.model.sign.SealUser;
import com.hfi.insurance.model.sign.TemplateFlowBean;
import com.hfi.insurance.model.sign.TemplateFormBean;
import com.hfi.insurance.model.sign.TemplateInfoBean;
import com.hfi.insurance.model.sign.req.CreateSignFlowReq;
import com.hfi.insurance.model.sign.req.FlowDocBean;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionReq;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionV2Model;
import com.hfi.insurance.model.sign.req.QueryInnerAccountsReq;
import com.hfi.insurance.model.sign.req.SignInfoBeanV2;
import com.hfi.insurance.model.sign.req.SingerInfo;
import com.hfi.insurance.model.sign.req.StandardCreateFlowBO;
import com.hfi.insurance.model.sign.req.StandardSignDocBean;
import com.hfi.insurance.model.sign.req.StandardSignerInfoBean;
import com.hfi.insurance.model.sign.req.TemplateFormValueParam;
import com.hfi.insurance.model.sign.req.TemplateUseParam;
import com.hfi.insurance.service.IYbFlowInfoService;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.service.SignedBizService;
import com.hfi.insurance.service.SignedService;
import com.hfi.insurance.utils.EnumHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private IYbFlowInfoService flowInfoService;

    @Resource
    private OrganizationsService organizationsService;

    @Resource
    private IYbInstitutionInfoService institutionInfoService;

    @Resource
    private Cache<String, String> caffeineCache;

    @Override
    @LogAnnotation
    public ApiResponse createSignFlow(CreateSignFlowReq req, String token) {
        String jsonStr = caffeineCache.asMap().get(token);
        if (StringUtils.isBlank(jsonStr)) {
            return new ApiResponse(ErrorCodeEnum.TOKEN_EXPIRED.getCode(), ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String institutionNumber = jsonObject.getString("number");
        log.info("从token中获取机构编号：{}", institutionNumber);
        String organizeNo = jsonObject.getString("areaCode");
        log.info("从token中获取区域号：{}", organizeNo);
//        String organizeNo = (String) session.getAttribute("areaCode");
//        String institutionNumber = (String) session.getAttribute("number");
        ETemplateType templateType = EnumHelper.translate(ETemplateType.class, req.getTemplateType());
        //填充模板的形式发起签署
        if (ETemplateType.TEMPLATE_FILL == templateType) {
            List<SingerInfo> singerInfos = req.getSingerInfos();
            Map<String, List<InstitutionBaseInfo>> flowNameInstitutionMap = new HashMap<>();
            Map<Integer, String> flowNameSizeMap = new LinkedHashMap<>(16);
            List<String> institutionNames = new ArrayList<>();
            List<InstitutionBaseInfo> institutionInfos = new ArrayList<>();
            singerInfos.forEach(singerInfo -> {
                int size = singerInfo.getInstitutionInfoList().size();
                flowNameSizeMap.put(size, singerInfo.getFlowName());
                flowNameInstitutionMap.put(singerInfo.getFlowName(), singerInfo.getInstitutionInfoList());
            });
            Integer maxSize = flowNameSizeMap.keySet().stream().max(Integer::compareTo).get();
            String maxSizeFlowName = flowNameSizeMap.get(maxSize);
            log.info("拥有{}个机构的签署方：{}", maxSize, maxSizeFlowName);
            String templateId = req.getTemplateId();
            //获取模板信息
            JSONObject templateInfoJson = signedService.getTemplateInfo(templateId);
            String templateStr = templateInfoJson.getString("template");
            log.info("模板信息：{}", templateStr);
            TemplateInfoBean templateInfo = JSON.parseObject(templateStr, TemplateInfoBean.class);
            if (templateInfo == null) {
                return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), "模板信息为空！！");
            }
            //乙方（3）* （丙方+丁方）(1) =3 (个流程)
            for (int i = 0; i < maxSize; i++) {
                StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
                //文档信息
                List<FlowDocBean> signDocs = new ArrayList<>();
                FlowDocBean flowDocBean = null;
                try {
                    flowDocBean = assembleSignDocs(req, templateInfo, flowNameInstitutionMap, maxSizeFlowName, i, organizeNo);
                } catch (BizServiceException e) {
                    return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), e.getMessage());
                }
                String fileKey = flowDocBean.getDocFilekey();
                signDocs.add(flowDocBean);
                standardCreateFlow.setSignDocs(signDocs);
                //签署人信息
                List<TemplateFlowBean> templateFlows = templateInfo.getTemplateFlows();
                Map<String, PredefineBean> flowNamePredefineMap = new HashMap<>();
                templateFlows.forEach(templateFlowBean -> {
                    flowNamePredefineMap.put(templateFlowBean.getFlowName(), templateFlowBean.getPredefine());
                });
                List<StandardSignerInfoBean> singerList = new ArrayList<>();
                //填充乙丙丁方签署信息
                for (SingerInfo singerInfo : req.getSingerInfos()) {
                    List<InstitutionBaseInfo> institutionInfoList = singerInfo.getInstitutionInfoList();
                    //获取签署机构名称
                    String singerNames = institutionInfoList.stream().map(InstitutionBaseInfo::getInstitutionName)
                            .collect(Collectors.joining(","));
                    institutionNames.add(singerNames);
                    InstitutionBaseInfo institution = null;
                    String flowName = singerInfo.getFlowName();
                    if (flowName.equals(maxSizeFlowName)) {
                        institution = institutionInfoList.get(i);
                    } else {
                        institution = CollectionUtils.firstElement(institutionInfoList);
                    }
                    if (institution != null) {
                        institutionInfos.add(institution);
                        YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(institution.getNumber());
                        if (institutionInfo != null) {
//                            PredefineBean predefineBean = flowNamePredefineMap.get(flowName);
//                            log.info("位置信息：{}", JSON.toJSONString(predefineBean));
                            //填充签署人信息
                            StandardSignerInfoBean signerInfoBean = null;
                            try {
                                signerInfoBean = assembleStandardSignerInfoBean(institutionInfo, singerInfo, fileKey, flowNamePredefineMap, templateType, flowName);
                            } catch (BizServiceException e) {
                                return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), e.getMessage());
                            }
                            singerList.add(signerInfoBean);
                        }
                    }
                }
                //填充甲方信息
//                PredefineBean predefineBeanA = flowNamePredefineMap.get("甲方");
                //填充甲方信息
                StandardSignerInfoBean partyA = null;
                //StandardSignerInfoBean legalPartyA = null;
                try {
                    partyA = assemblePartyAInfo(flowNamePredefineMap, req.getPartyASignType(), fileKey, organizeNo, templateType);
                    //legalPartyA = assemblePartyAInfo(flowNamePredefineMap, req.getPartyASignType(), fileKey, organizeNo, templateType, false);
                } catch (Exception e) {
                    log.error("填充甲方信息失败：{}", e.getMessage());
                    e.printStackTrace();
                    return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), e.getMessage());
                }
                singerList.add(partyA);
                //singerList.add(legalPartyA);
                //发起人姓名不能为空
                String initiatorName = partyA.getAccountName();
                standardCreateFlow.setInitiatorName(initiatorName);
                //手机号或者邮箱
                standardCreateFlow.setInitiatorMobile(partyA.getContactMobile());
                standardCreateFlow.setInitiatorAccountId(partyA.getAccountId());
                standardCreateFlow.setSigners(singerList);
                //流程主题
                standardCreateFlow.setSubject(req.getTemplateId() + "-" + System.currentTimeMillis());
                log.info("创建流程入参：{}", JSON.toJSONString(standardCreateFlow));
                JSONObject signFlows = signedService.createSignFlows(standardCreateFlow);
                log.info("创建流程出参：{}", JSON.toJSONString(signFlows));
                if (signFlows.containsKey("errCode")) {
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), signFlows.getString("msg"));
                }
                String signFlowId = signFlows.getString("signFlowId");
                List<InstitutionBaseInfo> distinctInstitutions = institutionInfos.stream().distinct().collect(Collectors.toList());
                List<YbFlowInfo> flowInfoList = new ArrayList<>();
                String singerName = String.join(",", institutionNames);
                String subject = req.getTemplateId() + "-" + System.currentTimeMillis();
                Date now = new Date();
                distinctInstitutions.forEach(institutionInfo -> {
                    YbFlowInfo flowInfo = new YbFlowInfo();
                    flowInfo.setInitiator(initiatorName)
                            .setNumber(institutionInfo.getNumber())
                            .setSigners(singerName)
                            .setSubject(subject)
                            .setCopyViewers(singerName)
                            .setSignFlowId(signFlowId)
                            .setInitiatorTime(now)
                            .setAccountType(2)
                            .setFlowType("Common");
                    flowInfoList.add(flowInfo);
                });
                YbFlowInfo flowAInfo = new YbFlowInfo();
                flowAInfo.setInitiator(initiatorName)
                        .setNumber(organizeNo)
                        .setSigners(singerName)
                        .setSubject(subject)
                        .setCopyViewers(singerName)
                        .setSignFlowId(signFlowId)
                        .setInitiatorTime(now)
                        .setUniqueId(partyA.getUniqueId())
                        .setAccountType(1)
                        .setFlowType("Common");
                flowInfoList.add(flowAInfo);
                flowInfoService.saveBatch(flowInfoList);
            }
        } else if (ETemplateType.FILE_UPLOAD == templateType) {
            List<String> institutionNames = new ArrayList<>();
            List<InstitutionBaseInfo> institutionInfos = new ArrayList<>();
            StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
            //文档信息
            List<FlowDocBean> signDocs = new ArrayList<>();
            FlowDocBean flowDocBean = new FlowDocBean();
            String fileKey = req.getFileKey();
            flowDocBean.setDocFilekey(fileKey);
            flowDocBean.setDocName(req.getFileName());
            signDocs.add(flowDocBean);
            standardCreateFlow.setSignDocs(signDocs);
            List<StandardSignerInfoBean> singerList = new ArrayList<>();
            //填充乙丙丁方签署信息
            for (SingerInfo singerInfo : req.getSingerInfos()) {
                List<InstitutionBaseInfo> institutionInfoList = singerInfo.getInstitutionInfoList();
                //获取签署机构名称
                String singerNames = institutionInfoList.stream().map(InstitutionBaseInfo::getInstitutionName)
                        .collect(Collectors.joining(","));
                institutionNames.add(singerNames);
                InstitutionBaseInfo institution = null;
                String flowName = singerInfo.getFlowName();
                institution = CollectionUtils.firstElement(institutionInfoList);
                if (institution != null) {
                    institutionInfos.add(institution);
                    YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(institution.getNumber());
                    //填充签署人信息
                    StandardSignerInfoBean signerInfoBean = null;
                    try {
                        signerInfoBean = assembleStandardSignerInfoBean(institutionInfo, singerInfo, fileKey, null, templateType, flowName);
                    } catch (BizServiceException e) {
                        return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), e.getMessage());
                    }
                    singerList.add(signerInfoBean);
                }

            }
            StandardSignerInfoBean partyA = null;
            try {
                partyA = assemblePartyAInfo(null, req.getPartyASignType(), fileKey, organizeNo, templateType);
            } catch (Exception e) {
                log.error("填充甲方信息失败：{}", e.getMessage());
                e.printStackTrace();
                return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
            }
            singerList.add(partyA);
            //发起人姓名不能为空
            String initiatorName = partyA.getAccountName();
            standardCreateFlow.setInitiatorName(initiatorName);
            //手机号或者邮箱
            standardCreateFlow.setInitiatorMobile(partyA.getContactMobile());
            standardCreateFlow.setInitiatorAccountId(partyA.getAccountId());
            standardCreateFlow.setSigners(singerList);
            //流程主题
            standardCreateFlow.setSubject(req.getTemplateId() + "-" + System.currentTimeMillis());
            log.info("创建流程入参：{}", JSON.toJSONString(standardCreateFlow));
            JSONObject signFlows = signedService.createSignFlows(standardCreateFlow);
            log.info("创建流程出参：{}", JSON.toJSONString(signFlows));
            if (signFlows.containsKey("errCode")) {
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), signFlows.getString("msg"));
            }
            String signFlowId = signFlows.getString("signFlowId");
            List<InstitutionBaseInfo> distinctInstitutions = institutionInfos.stream().distinct().collect(Collectors.toList());
            List<YbFlowInfo> flowInfoList = new ArrayList<>();
            String singerName = String.join(",", institutionNames);
            String subject = req.getTemplateId() + "-" + System.currentTimeMillis();
            distinctInstitutions.forEach(institutionInfo -> {
                YbFlowInfo flowInfo = new YbFlowInfo();
                flowInfo.setInitiator(initiatorName)
                        .setNumber(institutionInfo.getNumber())
                        .setSigners(singerName)
                        .setSubject(subject)
                        .setCopyViewers(singerName)
                        .setSignFlowId(signFlowId)
                        .setNumber(institutionNumber)
                        .setAccountType(2)
                        .setFlowType("Common");
                flowInfoList.add(flowInfo);
            });
            YbFlowInfo flowAInfo = new YbFlowInfo();
            flowAInfo.setInitiator(initiatorName)
                    .setNumber(organizeNo)
                    .setSigners(singerName)
                    .setSubject(subject)
                    .setCopyViewers(singerName)
                    .setSignFlowId(signFlowId)
                    .setInitiatorTime(new Date())
                    .setUniqueId(partyA.getUniqueId())
                    .setAccountType(1)
                    .setFlowType("Common");
            flowInfoList.add(flowAInfo);
            flowInfoService.saveBatch(flowInfoList);
        } else {
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "文档类型不能为空！");
        }
        return new ApiResponse(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getMessage());
    }

    /**
     * 填充文档信息
     *
     * @param req
     * @param templateInfo
     * @param flowNameInstitutionMap
     * @param maxSizeFlowName
     * @param i
     * @return
     */
    private FlowDocBean assembleSignDocs(CreateSignFlowReq req, TemplateInfoBean templateInfo,
                                         Map<String, List<InstitutionBaseInfo>> flowNameInstitutionMap,
                                         String maxSizeFlowName, int i,
                                         String areaCode) throws BizServiceException {
        FlowDocBean flowDocBean = new FlowDocBean();
        TemplateUseParam templateUseParam = new TemplateUseParam();
        //填充表单信息（文本域）
        List<TemplateFormValueParam> templateFormValues = new ArrayList<>();
        for (TemplateFormBean templateForm : templateInfo.getTemplateForms()) {
            TemplateFormValueParam templateFormValueParam = new TemplateFormValueParam();
            templateFormValueParam.setFormId(templateForm.getFormId());
            //文本域名称要是甲方、乙方、丙方机构名称...格式
            String formName = templateForm.getFormName();
            String flowName = formName.substring(0, 2);
            log.info("文本域名称：{}", flowName);
            if (!flowNameInstitutionMap.containsKey(flowName)) {
                throw new BizServiceException("请选择" + flowName + "机构！");
            }
            List<InstitutionBaseInfo> institutionInfos = flowNameInstitutionMap.get(flowName);
            if (!CollectionUtils.isEmpty(institutionInfos)) {
                if (flowName.equals(maxSizeFlowName)) {
                    templateFormValueParam.setFormValue(institutionInfos.get(i).getInstitutionName());
                } else {
                    templateFormValueParam.setFormValue(institutionInfos.get(0).getInstitutionName());
                }
            }
            if ("甲方".equals(flowName)) {
                String name = getInstitutionNameByAreaCode(areaCode);
                if (StringUtils.isNotBlank(name)) {
                    templateFormValueParam.setFormValue(name);
                } else {
                    templateFormValueParam.setFormValue("esigntest测试医保局");
                }
            }
            templateFormValues.add(templateFormValueParam);
        }
        templateUseParam.setTemplateFormValues(templateFormValues);
        templateUseParam.setTemplateId(req.getTemplateId());
        JSONObject jsonObject = signedService.buildTemplateDoc(templateUseParam);
        log.info("填充模板后文档信息：{}", jsonObject);
        String fileKey = jsonObject.getString("fileKey");
        flowDocBean.setDocFilekey(fileKey);
        flowDocBean.setDocName(templateInfo.getTemplateName());
        return flowDocBean;
    }

    /**
     * 组装签署人信息(乙丙丁)
     *
     * @param institutionInfo
     * @param singerInfo
     * @param fileKey
     * @param flowNamePredefineMap
     * @return
     */
    private StandardSignerInfoBean assembleStandardSignerInfoBean(YbInstitutionInfo institutionInfo, SingerInfo singerInfo, String fileKey, Map<String, PredefineBean> flowNamePredefineMap, ETemplateType templateType, String flowName) throws BizServiceException {
        StandardSignerInfoBean signerInfoBean = new StandardSignerInfoBean();
        signerInfoBean.setAccountId(institutionInfo.getAccountId());
        signerInfoBean.setAuthorizationOrganizeId(institutionInfo.getOrganizeId());
        signerInfoBean.setAccountType(singerInfo.getAccountType());
        signerInfoBean.setLegalSignFlag(1);
        //签署文档信息
        List<StandardSignDocBean> signDocDetails = new ArrayList<>();
        StandardSignDocBean standardSignDocBean = new StandardSignDocBean();
        standardSignDocBean.setDocFilekey(fileKey);
        if (ETemplateType.TEMPLATE_FILL == templateType) {
            log.info("模板填充发起");
            ESignType signType = EnumHelper.translate(ESignType.class, singerInfo.getSignType());
            if (ESignType.DEFAULT_COORDINATE_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
                signerInfoBean.setAutoSign(true);
            } else {
                signerInfoBean.setAutoSign(false);
            }
            if (ESignType.MANUAL_KEY_WORD_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
                List<SignInfoBeanV2> signPos = new ArrayList<>();
                SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
                signInfoBeanV2.setKey(flowName);
                signInfoBeanV2.setSignType(4);
                signPos.add(signInfoBeanV2);
                standardSignDocBean.setSignPos(signPos);
            }
            PredefineBean predefineBean = flowNamePredefineMap.get(flowName);
            log.info("机构章签署位置信息：{}", JSON.toJSONString(predefineBean));
            PredefineBean legalPredefineBean = flowNamePredefineMap.get(flowName + "法人");
            log.info("法人章签署位置信息：{}", JSON.toJSONString(legalPredefineBean));
            //位置签署要匹配区域
            if ((ESignType.MANUAL_COORDINATE_SIGN == signType || ESignType.DEFAULT_COORDINATE_SIGN == signType)) {
                if (predefineBean != null && legalPredefineBean != null) {
                    List<Position> positions = predefineBean.getPositions();

                    List<Position> legalPositions = legalPredefineBean.getPositions();
                    List<SignInfoBeanV2> signPoList = new ArrayList<>();
                    List<SignInfoBeanV2> signPos = positions.stream().map(position -> {
                        SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
                        //签署方式1-单页签署
                        signInfoBeanV2.setSignType(1);
                        signInfoBeanV2.setPosX(Float.valueOf(position.getPosX()));
                        signInfoBeanV2.setPosY(Float.valueOf(position.getPosY()));
                        signInfoBeanV2.setPosPage(position.getPageNo());
                        signInfoBeanV2.setSignIdentity("ORGANIZE");
                        return signInfoBeanV2;
                    }).collect(Collectors.toList());
                    List<SignInfoBeanV2> legalSignPos = legalPositions.stream().map(position -> {
                        SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
                        //签署方式1-单页签署
                        signInfoBeanV2.setSignType(1);
                        signInfoBeanV2.setPosX(Float.valueOf(position.getPosX()));
                        signInfoBeanV2.setPosY(Float.valueOf(position.getPosY()));
                        signInfoBeanV2.setPosPage(position.getPageNo());
                        signInfoBeanV2.setSignIdentity("LEGAL");
                        return signInfoBeanV2;
                    }).collect(Collectors.toList());
                    signPoList.addAll(signPos);
                    signPoList.addAll(legalSignPos);
                    standardSignDocBean.setSignPos(signPoList);
                } else {
                    throw new BizServiceException("手动关键字签署和静默关键字签署，模板位置信息不能为空！");
                }
            }
            signDocDetails.add(standardSignDocBean);
            signerInfoBean.setSignDocDetails(signDocDetails);
            return signerInfoBean;
        } else {
            log.info("文件直传发起");
            List<SignInfoBeanV2> signPos = new ArrayList<>();
            SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
            signInfoBeanV2.setKey(singerInfo.getKey());
            signInfoBeanV2.setSignType(4);
            signInfoBeanV2.setSignIdentity("ORGANIZE");
            SignInfoBeanV2 legalSignInfoBeanV2 = new SignInfoBeanV2();
            legalSignInfoBeanV2.setKey(singerInfo.getKey() + "法人");
            legalSignInfoBeanV2.setSignType(4);
            signPos.add(signInfoBeanV2);
            signPos.add(legalSignInfoBeanV2);
            signInfoBeanV2.setSignIdentity("LEGAL");
            standardSignDocBean.setSignPos(signPos);
            signDocDetails.add(standardSignDocBean);
            signerInfoBean.setSignDocDetails(signDocDetails);
            return signerInfoBean;
        }
    }


    /**
     * 填充甲方信息
     *
     * @param flowNamePredefineMap
     * @param partyASignType
     * @param fileKey
     * @return
     */
    private StandardSignerInfoBean assemblePartyAInfo(Map<String, PredefineBean> flowNamePredefineMap, int partyASignType, String fileKey, String organizeNo, ETemplateType templateType) throws Exception {
        StandardSignerInfoBean partyA = new StandardSignerInfoBean();
//        partyA.setLegalSignFlag(1);
        log.info("发起方（甲方）机构编码：{}", organizeNo);
        JSONObject innerOrgans = organizationsService.queryInnerOrgans(organizeNo);
        log.info("甲方机构信息：{}", innerOrgans.toJSONString());
        QueryInnerAccountsReq queryInnerAccountsReq = new QueryInnerAccountsReq();
        String organizeId = innerOrgans.getString("organizeId");
        partyA.setAuthorizationOrganizeId(organizeId);
        queryInnerAccountsReq.setOrganizeId(organizeId);
        queryInnerAccountsReq.setPageSize("10");
        queryInnerAccountsReq.setPageIndex("1");
        // 填充印章信息
        JSONObject innerOrgansSeals = signedService.getInnerOrgansSeals(organizeId, organizeNo);
        log.info("甲方印章信息：{}",innerOrgansSeals);
        if (innerOrgansSeals.containsKey("errCode")){
            throw new BizServiceException(innerOrgansSeals.getString("msg"));
        }
        String innerOrgansSealsStr = innerOrgansSeals.getString("seals");
        List<Seal> sealList = JSON.parseArray(innerOrgansSealsStr, Seal.class);
        Map<Integer, String> sealTypeAndSealIdMap = sealList.stream().collect(Collectors.toMap(Seal::getSubSealTypeId, Seal::getSealId));
        JSONObject sealInfos = signedService.getSealInfos(sealTypeAndSealIdMap.get(1));
        log.info("印章管理员信息：{}",sealInfos);
        if (innerOrgansSeals.containsKey("errCode")){
            throw new BizServiceException(innerOrgansSeals.getString("msg"));
        }
        String sealUsers = sealInfos.getString("sealUsers");
        List<SealUser> sealUserList = JSON.parseArray(sealUsers, SealUser.class);
        SealUser sealUser = CollectionUtils.firstElement(sealUserList);
        if (sealUser != null){
            partyA.setAccountId(sealUser.getAccountId());
            partyA.setAccountName(sealUser.getAccountName());
        }
        partyA.setAccountType(1);
        ESignType signType = EnumHelper.translate(ESignType.class, partyASignType);
        //签署文档信息;指定文档进行签署，未指定的文档将作为只读
        List<StandardSignDocBean> signDocDetails = new ArrayList<>();
        StandardSignDocBean standardSignDocBean = new StandardSignDocBean();
        standardSignDocBean.setDocFilekey(fileKey);

        //模板填充发起
        if (ETemplateType.TEMPLATE_FILL == templateType) {
            if (ESignType.DEFAULT_COORDINATE_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
                partyA.setAutoSign(true);
            } else {
                partyA.setAutoSign(false);
            }
            if (ESignType.MANUAL_KEY_WORD_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
                List<SignInfoBeanV2> signPos = new ArrayList<>();
                SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
                //签署类型；1-单页签、2-多页签、3-骑缝章、4关键字签
                signInfoBeanV2.setSignType(4);
                signInfoBeanV2.setKey("甲方");
                signPos.add(signInfoBeanV2);
                standardSignDocBean.setSignPos(signPos);
            }
            //位置签署要匹配区域
            PredefineBean predefineBeanA = flowNamePredefineMap.get("甲方");
            PredefineBean legalPredefineBeanA = flowNamePredefineMap.get("甲方法人");
            if (ESignType.MANUAL_COORDINATE_SIGN == signType || ESignType.DEFAULT_COORDINATE_SIGN == signType) {
                if (predefineBeanA != null && legalPredefineBeanA != null) {
                    List<Position> positions = predefineBeanA.getPositions();
                    List<Position> legalPositions = legalPredefineBeanA.getPositions();
                    List<SignInfoBeanV2> signPoList = new ArrayList<>();
                    List<SignInfoBeanV2> signPos = positions.stream().map(position -> {
                        SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
                        //签署方式1-单页签署
                        signInfoBeanV2.setSignType(1);
                        signInfoBeanV2.setPosX(Float.valueOf(position.getPosX()));
                        signInfoBeanV2.setPosY(Float.valueOf(position.getPosY()));
                        signInfoBeanV2.setPosPage(position.getPageNo());
//                        signInfoBeanV2.setSignIdentity("ORGANIZE");
                        signInfoBeanV2.setSealId(sealTypeAndSealIdMap.get(1));
                        return signInfoBeanV2;
                    }).collect(Collectors.toList());
                    List<SignInfoBeanV2> legalSignPos = legalPositions.stream().map(position -> {
                        SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
                        //签署方式1-单页签署
                        signInfoBeanV2.setSignType(1);
                        signInfoBeanV2.setPosX(Float.valueOf(position.getPosX()));
                        signInfoBeanV2.setPosY(Float.valueOf(position.getPosY()));
                        signInfoBeanV2.setPosPage(position.getPageNo());
//                        signInfoBeanV2.setSignIdentity("LEGAL");
                        signInfoBeanV2.setSealId(sealTypeAndSealIdMap.get(3));
                        return signInfoBeanV2;
                    }).collect(Collectors.toList());
                    signPoList.addAll(signPos);
                    signPoList.addAll(legalSignPos);
                    standardSignDocBean.setSignPos(signPoList);
                } else {
                    throw new BizServiceException("手动关键字签署和静默关键字签署，模板位置信息不能为空！");
                }
            }
            signDocDetails.add(standardSignDocBean);
            partyA.setSignDocDetails(signDocDetails);
        }else {
            //文件直传发起
            List<SignInfoBeanV2> signPos = new ArrayList<>();
            SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
            signInfoBeanV2.setKey("甲方");
            signInfoBeanV2.setSignType(4);
            signInfoBeanV2.setSignIdentity("ORGANIZE");
            SignInfoBeanV2 legalSignInfoBeanV2 = new SignInfoBeanV2();
            legalSignInfoBeanV2.setKey("甲方法人");
            legalSignInfoBeanV2.setSignType(4);
            signPos.add(signInfoBeanV2);
            signPos.add(legalSignInfoBeanV2);
            standardSignDocBean.setSignPos(signPos);
            signDocDetails.add(standardSignDocBean);
            partyA.setSignDocDetails(signDocDetails);
        }
        return partyA;
    }

    /**
     * 填充位置信息
     * @param predefineBean
     * @param signType
     * @param signIdentity
     * @return
     */
    private List<SignInfoBeanV2> assembleSignPoList(PredefineBean predefineBean, int signType, String signIdentity) {
        List<Position> positions = predefineBean.getPositions();
        List<SignInfoBeanV2> signPos = positions.stream().map(position -> {
            SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
            signInfoBeanV2.setSignType(signType);
            signInfoBeanV2.setPosX(Float.valueOf(position.getPosX()));
            signInfoBeanV2.setPosY(Float.valueOf(position.getPosY()));
            signInfoBeanV2.setPosPage(position.getPageNo());
            signInfoBeanV2.setSignIdentity(signIdentity);
            return signInfoBeanV2;
        }).collect(Collectors.toList());
        return signPos;
    }

    /**
     * 获取机构名称
     *
     * @param areaCode
     * @return
     */
    private String getInstitutionNameByAreaCode(String areaCode) {
        String orgName = null;
        //主城区医保局330100、
        // 萧山区医保局330109、余杭区医保局330110、富阳区医保局330183、临安区医保局330185、
        // 桐庐县医保局330122、淳安县医保局330127、建德市医保局330182、临平区医保局330113
        switch (areaCode) {
            case "330100":
                orgName = "主城区医保局";
                break;
            case "330109":
                orgName = "萧山区医保局";
                break;
            case "330110":
                orgName = "余杭区医保局";
                break;
            case "330183":
                orgName = "富阳区医保局";
                break;
            case "330185":
                orgName = "临安区医保局";
                break;
            case "330122":
                orgName = "桐庐县医保局";
                break;
            case "330127":
                orgName = "淳安县医保局";
                break;
            case "330182":
                orgName = "建德市医保局";
                break;
            case "330113":
                orgName = "临平区医保局";
                break;
            default:
                break;
        }
        return orgName;
    }

    @Override
    @LogAnnotation
    public ApiResponse getPageWithPermission(GetPageWithPermissionReq req, String token) {
        String jsonStr = caffeineCache.asMap().get(token);
        if (StringUtils.isBlank(jsonStr)) {
            return new ApiResponse(ErrorCodeEnum.TOKEN_EXPIRED.getCode(), ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String organizeNo = jsonObject.getString("areaCode");
        log.info("从token中获取区域号：{}", organizeNo);
        JSONObject innerOrgans = organizationsService.queryInnerOrgans(organizeNo);
        if (innerOrgans.containsKey("errCode")) {
            log.error("查询内部机构信息信息异常，{}", innerOrgans);
            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), innerOrgans.getString("msg"));
        }
        GetPageWithPermissionV2Model getPageWithPermissionV2Model = new GetPageWithPermissionV2Model();
        BeanUtils.copyProperties(req, getPageWithPermissionV2Model);
        getPageWithPermissionV2Model.setPageIndex(req.getPageNum());
        String innerOrganId = innerOrgans.getString("organizeId");
        log.info("内部机构id：{}", innerOrganId);
        getPageWithPermissionV2Model.setDepartmentId(innerOrganId);
        JSONObject result = signedService.getPageWithPermission(getPageWithPermissionV2Model);
        log.info("获取模板列表响应参数：{}", result);
        if (result.containsKey("errCode")) {
            log.error("查询模板信息异常，{}", result);
            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), result.getString("msg"));
        }
        if (result.containsKey("success")) {
            String templateInfos = result.getString("templateInfos");
            Page page = new Page();
            page.setTotal(result.getInteger("totalCount"));
            JSONArray data = JSON.parseArray(templateInfos);
            page.setRecords(data);
            return new ApiResponse(page);
        } else {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), ErrorCodeEnum.RESPONES_ERROR.getMessage());
        }
    }

    @Override
    @LogAnnotation
    public ApiResponse getTemplateInfo(String templateId) {
        JSONObject templateInfo = signedService.getTemplateInfo(templateId);
        Map<String, Integer> res = new HashMap<>();
        log.info("获取模板详细信息响应参数：{}", templateInfo);
        if (templateInfo.containsKey("errCode")) {
            log.error("查询模板信息异常，{}", templateInfo);
            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), templateInfo.getString("msg"));
        }
        if (templateInfo.containsKey("success")) {
            String templateInfoString = templateInfo.getString("template");
            TemplateInfoBean templateInfoBean = JSON.parseObject(templateInfoString, TemplateInfoBean.class);
            List<TemplateFlowBean> templateFlows = templateInfoBean.getTemplateFlows();
            List<PredefineBean> predefineList = templateFlows.stream().map(TemplateFlowBean::getPredefine).collect(Collectors.toList());
            Optional<PredefineBean> any = predefineList.stream().filter(predefineBean -> null != predefineBean.getPositions()).findAny();
            if (any.isPresent()) {
                //有坐标
                res.put("flag", 1);
                return new ApiResponse(res);
            } else {
                //无坐标
                res.put("flag", 0);
                return new ApiResponse(res);
            }
        } else {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), ErrorCodeEnum.RESPONES_ERROR.getMessage());
        }
    }
}
