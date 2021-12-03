package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.common.BizServiceException;
import com.hfi.insurance.enums.*;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.sign.*;
import com.hfi.insurance.model.sign.req.*;
import com.hfi.insurance.service.IYbFlowInfoService;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.service.SignedBizService;
import com.hfi.insurance.service.SignedService;
import com.hfi.insurance.utils.DateUtil;
import com.hfi.insurance.utils.EnumHelper;
import com.hfi.insurance.utils.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
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

    private int MAX_SIGNERSLENGTH = 490;

//    public static SignTimeBean signTimeBean = new SignTimeBean(Cons.DateFormatStr.CHINES_DATE_FORMAT);
//
//    public static List<SignTimeBean> signTimeBeanList = new ArrayList<SignTimeBean>() {
//        {
//            SignTimeBean signTimeBean = new SignTimeBean(Cons.DateFormatStr.CHINES_DATE_FORMAT);
//            add(signTimeBean);
//        }
//    };

//    @Override
//    @LogAnnotation
//    public ApiResponse createSignFlow(CreateSignFlowReq req, String token) {
//        String jsonStr = caffeineCache.asMap().get(token);
//        if (StringUtils.isBlank(jsonStr)) {
//            return new ApiResponse(ErrorCodeEnum.TOKEN_EXPIRED.getCode(), ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
//        }
//        JSONObject jsonObject = JSON.parseObject(jsonStr);
//        String institutionNumber = jsonObject.getString("number");
//        log.info("从token中获取机构编号：{}", institutionNumber);
//        String organizeNo = jsonObject.getString("areaCode");
//        log.info("从token中获取区域号：{}", organizeNo);
////        String organizeNo = (String) session.getAttribute("areaCode");
////        String institutionNumber = (String) session.getAttribute("number");
//        ETemplateType templateType = EnumHelper.translate(ETemplateType.class, req.getTemplateType());
//        //填充模板的形式发起签署
//        if (ETemplateType.TEMPLATE_FILL == templateType) {
//            List<SingerInfo> singerInfos = req.getSingerInfos();
//            Map<String, List<InstitutionBaseInfo>> flowNameInstitutionMap = new HashMap<>();
//            Map<Integer, String> flowNameSizeMap = new LinkedHashMap<>(16);
//            List<String> institutionNames = new ArrayList<>();
//            List<InstitutionBaseInfo> institutionInfos = new ArrayList<>();
//            singerInfos.forEach(singerInfo -> {
//                int size = singerInfo.getInstitutionInfoList().size();
//                flowNameSizeMap.put(size, singerInfo.getFlowName());
//                flowNameInstitutionMap.put(singerInfo.getFlowName(), singerInfo.getInstitutionInfoList());
//            });
//            Integer maxSize = flowNameSizeMap.keySet().stream().max(Integer::compareTo).get();
//            String maxSizeFlowName = flowNameSizeMap.get(maxSize);
//            log.info("拥有{}个机构的签署方：{}", maxSize, maxSizeFlowName);
//            String templateId = req.getTemplateId();
//            //获取模板信息
//            JSONObject templateInfoJson = signedService.getTemplateInfo(templateId);
//            String templateStr = templateInfoJson.getString("template");
//            log.info("模板信息：{}", templateStr);
//            TemplateInfoBean templateInfo = JSON.parseObject(templateStr, TemplateInfoBean.class);
//            if (templateInfo == null) {
//                return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), "模板信息为空！！");
//            }
//            //乙方（3）* （丙方+丁方）(1) =3 (个流程)
//            for (int i = 0; i < maxSize; i++) {
//                StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
//                //1、文档信息
//                List<FlowDocBean> signDocs = new ArrayList<>();
//                //2、抄送人信息集合
//                List<CopyViewerInfoBean> copyViewerInfoBeans = new ArrayList<>();
//                FlowDocBean flowDocBean = null;
//                try {
//                    flowDocBean = assembleSignDocs(req, templateInfo, flowNameInstitutionMap, maxSizeFlowName, i, organizeNo);
//                } catch (BizServiceException e) {
//                    return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), e.getMessage());
//                }
//                String fileKey = flowDocBean.getDocFilekey();
//                signDocs.add(flowDocBean);
//                standardCreateFlow.setSignDocs(signDocs);
//                List<TemplateFlowBean> templateFlows = templateInfo.getTemplateFlows();
//                Map<String, PredefineBean> flowNamePredefineMap = new HashMap<>();
//                templateFlows.forEach(templateFlowBean -> {
//                    flowNamePredefineMap.put(templateFlowBean.getFlowName(), templateFlowBean.getPredefine());
//                });
//                //3、待优化 填充乙丙丁方签署机构信息
//                List<StandardSignerInfoBean> singerList = new ArrayList<>();
//                for (SingerInfo singerInfo : req.getSingerInfos()) {
//                    List<InstitutionBaseInfo> institutionInfoList = singerInfo.getInstitutionInfoList();
//                    //获取签署机构名称
//                    String singerNames = institutionInfoList.stream().map(InstitutionBaseInfo::getInstitutionName)
//                            .collect(Collectors.joining(","));
//                    institutionNames.add(singerNames);
//                    InstitutionBaseInfo institution = null;
//                    String flowName = singerInfo.getFlowName();
//                    if (flowName.equals(maxSizeFlowName)) {
//                        institution = institutionInfoList.get(i);
//                    } else {
//                        institution = CollectionUtils.firstElement(institutionInfoList);
//                    }
//                    if (institution != null) {
//                        institutionInfos.add(institution);
//                        //填充签署人信息
//                        StandardSignerInfoBean signerInfoBean = null;
//                        try {
//                            CopyViewerInfoBean copyViewerInfoBean = new CopyViewerInfoBean();
//                            YbInstitutionInfo institutionInfo = getInstitutionInfo(institution);
//                            log.info("签署机构信息：{}",JSON.toJSONString(institutionInfo));
//                            if (!institutionInfo.getNumber().startsWith("bx")) {
//                                copyViewerInfoBean.setAccountId(institutionInfo.getAccountId());
//                                copyViewerInfoBean.setAccountType(EAccountType.EXTERNAL.getCode());
//                                copyViewerInfoBeans.add(copyViewerInfoBean);
//                            }
//                            signerInfoBean = assembleStandardSignerInfoBean(institutionInfo, singerInfo, fileKey, flowNamePredefineMap, templateType, flowName);
//                        } catch (BizServiceException e) {
//                            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), e.getMessage());
//                        }
//                        singerList.add(signerInfoBean);
//                    }
//                }
//                //填充甲方信息
//                StandardSignerInfoBean partyA = null;
//                //StandardSignerInfoBean legalPartyA = null;
//                try {
//                    partyA = assemblePartyAInfo(flowNamePredefineMap, req.getPartyASignType(), fileKey, organizeNo, templateType);
//                    //legalPartyA = assemblePartyAInfo(flowNamePredefineMap, req.getPartyASignType(), fileKey, organizeNo, templateType, false);
//                } catch (Exception e) {
//                    log.error("填充甲方信息失败：{}", e.getMessage());
//                    e.printStackTrace();
//                    return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), e.getMessage());
//                }
//                singerList.add(partyA);
//                //singerList.add(legalPartyA);
//                //发起人姓名不能为空
//                String initiatorName = partyA.getAccountName();
//                standardCreateFlow.setInitiatorName(initiatorName);
//                standardCreateFlow.setCopyViewers(copyViewerInfoBeans);
//                //手机号或者邮箱
//                standardCreateFlow.setInitiatorMobile(partyA.getContactMobile());
//                standardCreateFlow.setInitiatorAccountId(partyA.getAccountId());
//                standardCreateFlow.setSigners(singerList);
//                //流程主题
//                Date now = new Date();
//                String subject = req.getTemplateId() + "-" + flowDocBean.getDocName() + "-" + DateUtil.getNowTimestampStr();
//                standardCreateFlow.setSubject(subject);
//                JSONObject signFlows = signedService.createSignFlows(standardCreateFlow);
//                log.info("创建流程出参：{}", JSON.toJSONString(signFlows));
//                if (signFlows.containsKey("errCode")) {
//                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), signFlows.getString("msg"));
//                }
//                String signFlowId = signFlows.getString("signFlowId");
//                List<InstitutionBaseInfo> distinctInstitutions = institutionInfos.stream().distinct().collect(Collectors.toList());
//                List<YbFlowInfo> flowInfoList = new ArrayList<>();
//                String singerName = String.join(",", institutionNames);
//                distinctInstitutions.forEach(institutionInfo -> {
//                    YbFlowInfo flowInfo = new YbFlowInfo();
//                    flowInfo.setInitiator(initiatorName)
//                            .setNumber(institutionInfo.getNumber())
//                            .setSigners(singerName)
//                            .setSubject(subject)
//                            .setCopyViewers(singerName)
//                            .setSignFlowId(signFlowId)
//                            .setFileKey(fileKey)
//                            .setInitiatorTime(now)
//                            .setAccountType(2)
//                            .setFlowStatus(0)
//                            .setSignStatus("0")
//                            .setFlowType("Common");
//                    flowInfoList.add(flowInfo);
//                });
//                YbFlowInfo flowAInfo = new YbFlowInfo();
//                flowAInfo.setInitiator(initiatorName)
//                        .setNumber(organizeNo)
//                        .setSigners(singerName)
//                        .setSubject(subject)
//                        .setCopyViewers(singerName)
//                        .setSignFlowId(signFlowId)
//                        .setFileKey(fileKey)
//                        .setInitiatorTime(now)
//                        .setUniqueId(partyA.getUniqueId())
//                        .setAccountType(1)
//                        .setFlowStatus(0)
//                        .setSignStatus("0")
//                        .setFlowType("Common");
//                flowInfoList.add(flowAInfo);
//                flowInfoService.saveBatch(flowInfoList);
//            }
//        } else if (ETemplateType.FILE_UPLOAD == templateType) {
//            List<String> institutionNames = new ArrayList<>();
//            List<InstitutionBaseInfo> institutionInfos = new ArrayList<>();
//            StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
//            //文档信息
//            List<FlowDocBean> signDocs = new ArrayList<>();
//            //2、抄送人信息集合
//            List<CopyViewerInfoBean> copyViewerInfoBeans = new ArrayList<>();
//            FlowDocBean flowDocBean = new FlowDocBean();
//            String fileKey = req.getFileKey();
//            flowDocBean.setDocFilekey(fileKey);
//            flowDocBean.setDocName(req.getFileName());
//            signDocs.add(flowDocBean);
//            standardCreateFlow.setSignDocs(signDocs);
//            List<StandardSignerInfoBean> singerList = new ArrayList<>();
//            //填充乙丙丁方签署信息
//            for (SingerInfo singerInfo : req.getSingerInfos()) {
//                List<InstitutionBaseInfo> institutionInfoList = singerInfo.getInstitutionInfoList();
//                //获取签署机构名称
//                String singerNames = institutionInfoList.stream().map(InstitutionBaseInfo::getInstitutionName)
//                        .collect(Collectors.joining(","));
//                institutionNames.add(singerNames);
//                InstitutionBaseInfo institution = null;
//                String flowName = singerInfo.getFlowName();
//                institution = CollectionUtils.firstElement(institutionInfoList);
//                if (institution != null) {
//                    institutionInfos.add(institution);
//                    //填充签署人信息
//                    StandardSignerInfoBean signerInfoBean = null;
//                    try {
//                        CopyViewerInfoBean copyViewerInfoBean = new CopyViewerInfoBean();
//                        YbInstitutionInfo institutionInfo = getInstitutionInfo(institution);
//                        log.info("签署机构信息：{}",JSON.toJSONString(institutionInfo));
//                        if (!institutionInfo.getNumber().startsWith("bx")) {
//                            copyViewerInfoBean.setAccountId(institutionInfo.getAccountId());
//                            copyViewerInfoBean.setAccountType(EAccountType.EXTERNAL.getCode());
//                            copyViewerInfoBeans.add(copyViewerInfoBean);
//                        }
//                        signerInfoBean = assembleStandardSignerInfoBean(institutionInfo, singerInfo, fileKey, null, templateType, flowName);
//                    } catch (BizServiceException e) {
//                        return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), e.getMessage());
//                    }
//                    singerList.add(signerInfoBean);
//                }
//
//            }
//            StandardSignerInfoBean partyA = null;
//            try {
//                partyA = assemblePartyAInfo(null, req.getPartyASignType(), fileKey, organizeNo, templateType);
//            } catch (Exception e) {
//                log.error("填充甲方信息失败：{}", e.getMessage());
//                e.printStackTrace();
//                return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
//            }
//            singerList.add(partyA);
//            standardCreateFlow.setCopyViewers(copyViewerInfoBeans);
//            //发起人姓名不能为空
//            String initiatorName = partyA.getAccountName();
//            standardCreateFlow.setInitiatorName(initiatorName);
//            //手机号或者邮箱
//            standardCreateFlow.setInitiatorMobile(partyA.getContactMobile());
//            standardCreateFlow.setInitiatorAccountId(partyA.getAccountId());
//            standardCreateFlow.setSigners(singerList);
//            //流程主题
//            String subject = req.getTemplateId() + "-" + req.getFileName() + "-" + DateUtil.getNowTimestampStr();
//            standardCreateFlow.setSubject(subject);
//            log.info("创建流程入参：{}", JSON.toJSONString(standardCreateFlow));
//            JSONObject signFlows = signedService.createSignFlows(standardCreateFlow);
//            log.info("创建流程出参：{}", JSON.toJSONString(signFlows));
//            if (signFlows.containsKey("errCode")) {
//                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), signFlows.getString("msg"));
//            }
//            String signFlowId = signFlows.getString("signFlowId");
//            List<InstitutionBaseInfo> distinctInstitutions = institutionInfos.stream().distinct().collect(Collectors.toList());
//            List<YbFlowInfo> flowInfoList = new ArrayList<>();
//            String singerName = String.join(",", institutionNames);
//            distinctInstitutions.forEach(institutionInfo -> {
//                YbFlowInfo flowInfo = new YbFlowInfo();
//                flowInfo.setInitiator(initiatorName)
//                        .setNumber(institutionInfo.getNumber())
//                        .setSigners(singerName)
//                        .setSubject(subject)
//                        .setCopyViewers(singerName)
//                        .setSignFlowId(signFlowId)
//                        .setNumber(institutionNumber)
//                        .setAccountType(2)
//                        .setFlowStatus(0)
//                        .setSignStatus("0")
//                        .setFileKey(fileKey)
//                        .setFlowType("Common");
//                flowInfoList.add(flowInfo);
//            });
//            YbFlowInfo flowAInfo = new YbFlowInfo();
//            flowAInfo.setInitiator(initiatorName)
//                    .setNumber(organizeNo)
//                    .setSigners(singerName)
//                    .setSubject(subject)
//                    .setCopyViewers(singerName)
//                    .setSignFlowId(signFlowId)
//                    .setInitiatorTime(new Date())
//                    .setUniqueId(partyA.getUniqueId())
//                    .setAccountType(1)
//                    .setFlowStatus(0)
//                    .setSignStatus("0")
//                    .setFileKey(fileKey)
//                    .setFlowType("Common");
//            flowInfoList.add(flowAInfo);
//            flowInfoService.saveBatch(flowInfoList);
//        } else {
//            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "文档类型不能为空！");
//        }
//        return new ApiResponse(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getMessage());
//    }

    @Override
    @LogAnnotation
    public ApiResponse createSignFlow(CreateSignFlowReq req, String token) {
        log.info("0.createSignFlow 批量签署入参：{}", JSONObject.toJSONString(req));
        String jsonStr = caffeineCache.asMap().get(token);
        if (StringUtils.isBlank(jsonStr)) {
            return new ApiResponse(ErrorCodeEnum.TOKEN_EXPIRED.getCode(), ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String institutionNumber = jsonObject.getString("number");

        log.info("1.从token中获取机构编号：{}", institutionNumber);
        String organizeNo = jsonObject.getString("areaCode");
        log.info("2.从token中获取区域号：{}", organizeNo);
//        String organizeNo = (String) session.getAttribute("areaCode");
//        String institutionNumber = (String) session.getAttribute("number");
        ETemplateType templateType = EnumHelper.translate(ETemplateType.class, req.getTemplateType());
        StringBuilder errMsg = new StringBuilder();
        //用于记录填充number-甲乙丙丁方关系
        Map<String, String> flowNameMap = new HashMap<>();
        String subjectSuffix = "-" + DateUtil.getNowTimestampStr();
        //填充模板的形式发起签署
        if (ETemplateType.TEMPLATE_FILL == templateType) {
            //所有签署人员信息，内容来源为前端传入
            List<SingerInfo> singerInfos = req.getSingerInfos();
            //k=机构签署区域 甲方,丙方,乙方，外部传入，v=机构天印系统经办人信息
            Map<String, List<InstitutionBaseInfo>> flowNameInstitutionMap = new HashMap<>();
            //签署人名字
            Map<Integer, String> flowNameSizeMap = new LinkedHashMap<>(16);
            //获取所有签署机构名称，并使用','连接起来
            Map<String, String> institutionNameMap = new HashMap<>();
//            List<InstitutionBaseInfo> institutionInfos = new ArrayList<>();
            singerInfos.forEach(singerInfo -> {
                int size = singerInfo.getInstitutionInfoList().size();
                flowNameSizeMap.put(size, singerInfo.getFlowName());
                flowNameInstitutionMap.put(singerInfo.getFlowName(), singerInfo.getInstitutionInfoList());
                //填充number-甲乙丙丁方关系，为了处理批量落库
                for (int i = 0; i < size; i++) {
                    InstitutionBaseInfo institutionBaseInfo = singerInfo.getInstitutionInfoList().get(i);
                    flowNameMap.put(institutionBaseInfo.getNumber(), singerInfo.getFlowName());

                }
            });
            //找出批量的机构
            Integer maxSize = flowNameSizeMap.keySet().stream().max(Integer::compareTo).get();
            String maxSizeFlowName = flowNameSizeMap.get(maxSize);
            log.info("3.拥有{}个机构的签署方：{}", maxSize, maxSizeFlowName);
            String templateId = req.getTemplateId();
            //获取模板信息
            JSONObject templateInfoJson = signedService.getTemplateInfo(templateId);
            String templateStr = templateInfoJson.getString("template");
            log.info("4.模板信息：{}", templateStr);
            //从E签宝获取模板信息
            TemplateInfoBean templateInfo = JSON.parseObject(templateStr, TemplateInfoBean.class);
            if (templateInfo == null) {
                return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), "4.1模板信息为空！！");
            }
            //乙方（3）* （丙方+丁方）(1) =3 (个流程)
            for (int i = 0; i < maxSize; i++) {
//                StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
                //1、文档信息
                List<FlowDocBean> signDocs = new ArrayList<>();
                //2、抄送人信息集合
                List<CopyViewerInfoBean> copyViewerInfoBeans = new ArrayList<>();

                FlowDocBean flowDocBean = null;

                try {
                    //填充模板信息
                    flowDocBean = assembleSignDocs(req, templateInfo, flowNameInstitutionMap, maxSizeFlowName, i, organizeNo);
                } catch (BizServiceException e) {
                    return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), e.getMessage());
                }
                String fileKey = flowDocBean.getDocFilekey();
                if (StringUtils.isBlank(fileKey)) {
                    return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(), "获取文档信息失败，fileKey=" + fileKey);
                }
                signDocs.add(flowDocBean);
//                standardCreateFlow.setSignDocs(signDocs);
                List<TemplateFlowBean> templateFlows = templateInfo.getTemplateFlows();
                Map<String, PredefineBean> flowNamePredefineMap = new HashMap<>();
                templateFlows.forEach(templateFlowBean -> {
                    flowNamePredefineMap.put(templateFlowBean.getFlowName(), templateFlowBean.getPredefine());
                });
                //3、待优化 填充乙丙丁方签署机构信息
                List<StandardSignerInfoBean> singerList = new ArrayList<>();
                //数据库记录此次签署方（乙丙丁……信息），甲方信息另外处理
                List<InstitutionBaseInfo> institutionInfos = new ArrayList<>();
                for (SingerInfo singerInfo : req.getSingerInfos()) {
                    List<InstitutionBaseInfo> institutionInfoList = singerInfo.getInstitutionInfoList();
                    //获取签署机构名称，并拼接，
//                    StringBuilder nameSb = new StringBuilder();
                    institutionInfoList.forEach(info -> {
                        //存储机构名称和名字
                        institutionNameMap.put(info.getNumber(), info.getInstitutionName());
//                        nameSb.append(info.getInstitutionName()).append(",");
                    });
//                    //去掉最后的“,”
//                    if (nameSb.length() > 0) {
//                        nameSb.deleteCharAt(nameSb.length() - 1);
//                    }
//                    String singerNames = institutionInfoList.stream().map(InstitutionBaseInfo::getInstitutionName)
//                            .collect(Collectors.joining(","));
//                    institutionNames.add(singerNames);
                    InstitutionBaseInfo institution = null;
                    String flowName = singerInfo.getFlowName();
                    if (flowName.equals(maxSizeFlowName)) {
                        institution = institutionInfoList.get(i);
                    } else {
                        institution = CollectionUtils.firstElement(institutionInfoList);
                    }
                    if (institution != null) {
                        institutionInfos.add(institution);
                        //填充签署人信息
                        StandardSignerInfoBean signerInfoBean = null;
                        try {
                            CopyViewerInfoBean copyViewerInfoBean = new CopyViewerInfoBean();
                            //通过id和mumber去E签宝查询机构列表
                            YbInstitutionInfo institutionInfo = getInstitutionInfo(institution);
                            log.info("5.1签署机构信息：{}", JSON.toJSONString(institutionInfo));
                            if (!institutionInfo.getNumber().startsWith("bx")) {
                                copyViewerInfoBean.setAccountId(institutionInfo.getAccountId());
                                copyViewerInfoBean.setAccountType(EAccountType.EXTERNAL.getCode());
                                copyViewerInfoBeans.add(copyViewerInfoBean);
                            }
                            signerInfoBean = assembleStandardSignerInfoBean(institutionInfo, singerInfo, fileKey
                                    , flowNamePredefineMap, templateType, flowName, req.isAddSignTime());
                        } catch (BizServiceException e) {
                            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), e.getMessage());
                        }
                        singerList.add(signerInfoBean);
                    }
                }
                //填充甲方信息
                StandardSignerInfoBean partyA = null;
                //StandardSignerInfoBean legalPartyA = null;
                try {
                    flowNameMap.put(organizeNo, "甲方");
                    partyA = assemblePartyAInfo(flowNamePredefineMap, req.getPartyASignType(), fileKey, organizeNo, templateType, req.isAddSignTime());
                    //legalPartyA = assemblePartyAInfo(flowNamePredefineMap, req.getPartyASignType(), fileKey, organizeNo, templateType, false);
                } catch (Exception e) {
                    log.error("6.1填充甲方信息失败：{}", e.getMessage());
                    e.printStackTrace();
                    return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), e.getMessage());
                }
                singerList.add(partyA);
                String subjectPrefix = req.getTemplateId() + "-" + flowDocBean.getDocName();
                partyA.setOrganizeNo(organizeNo);
                handleCreateSign(errMsg, institutionInfos, partyA, institutionNameMap, copyViewerInfoBeans
                        , subjectPrefix, subjectSuffix, fileKey, templateType, institutionNumber
                        , singerList, signDocs, flowNameMap);

            }
        } else if (ETemplateType.FILE_UPLOAD == templateType) {
            Map<String, String> institutionNameMap = new HashMap<>();
//            List<InstitutionBaseInfo> institutionInfos = new ArrayList<>();
//            StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
            //文档信息
            List<FlowDocBean> signDocs = new ArrayList<>();
            //2、抄送人信息集合
            List<CopyViewerInfoBean> copyViewerInfoBeans = new ArrayList<>();
            FlowDocBean flowDocBean = new FlowDocBean();
            String fileKey = req.getFileKey();
            flowDocBean.setDocFilekey(fileKey);
            flowDocBean.setDocName(req.getFileName());
            signDocs.add(flowDocBean);
//            standardCreateFlow.setSignDocs(signDocs);
            List<StandardSignerInfoBean> singerList = new ArrayList<>();
            //数据库记录此次签署方（乙丙丁……信息），甲方信息另外处理
            List<InstitutionBaseInfo> institutionInfos = new ArrayList<>();
            //填充乙丙丁方签署信息
            for (SingerInfo singerInfo : req.getSingerInfos()) {
                List<InstitutionBaseInfo> institutionInfoList = singerInfo.getInstitutionInfoList();
                //填充number-甲乙丙丁方关系，为了处理批量落库
                for (int i = 0; i < singerInfo.getInstitutionInfoList().size(); i++) {
                    InstitutionBaseInfo institutionBaseInfo = singerInfo.getInstitutionInfoList().get(i);
                    flowNameMap.put(institutionBaseInfo.getNumber(), singerInfo.getFlowName());
                }
//                StringBuilder nameSb = new StringBuilder();
                institutionInfoList.forEach(info -> {
                    //存储机构名称和名字
                    institutionNameMap.put(info.getNumber(), info.getInstitutionName());
//                    nameSb.append(info.getInstitutionName()).append(",");
                });
//                //去掉最后的“,”
//                if (nameSb.length() > 0) {
//                    nameSb.deleteCharAt(nameSb.length() - 1);
//                }
//                //获取签署机构名称
//                String singerNames = institutionInfoList.stream().map(InstitutionBaseInfo::getInstitutionName)
//                        .collect(Collectors.joining(","));
//                institutionNames.add(singerNames);
                InstitutionBaseInfo institution = null;
                String flowName = singerInfo.getFlowName();
                institution = CollectionUtils.firstElement(institutionInfoList);
                if (institution != null) {
                    institutionInfos.add(institution);
                    //填充签署人信息
                    StandardSignerInfoBean signerInfoBean = null;
                    try {
                        CopyViewerInfoBean copyViewerInfoBean = new CopyViewerInfoBean();
                        YbInstitutionInfo institutionInfo = getInstitutionInfo(institution);
                        log.info("5.2签署机构信息：{}", JSON.toJSONString(institutionInfo));
                        if (!institutionInfo.getNumber().startsWith("bx")) {
                            copyViewerInfoBean.setAccountId(institutionInfo.getAccountId());
                            copyViewerInfoBean.setAccountType(EAccountType.EXTERNAL.getCode());
                            copyViewerInfoBeans.add(copyViewerInfoBean);
                        }
                        signerInfoBean = assembleStandardSignerInfoBean(institutionInfo, singerInfo, fileKey
                                , null, templateType, flowName, req.isAddSignTime());
                    } catch (BizServiceException e) {
                        return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), e.getMessage());
                    }
                    singerList.add(signerInfoBean);
                }

            }
            StandardSignerInfoBean partyA = null;
            try {
                flowNameMap.put(organizeNo, "甲方");
                partyA = assemblePartyAInfo(null, req.getPartyASignType(), fileKey
                        , organizeNo, templateType, req.isAddSignTime());
            } catch (Exception e) {
                log.error("6.2填充甲方信息失败：{}", e.getMessage());
                e.printStackTrace();
                return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
            }
            singerList.add(partyA);
            String subjectPrefix = req.getTemplateId() + "-" + req.getFileName();

            handleCreateSign(errMsg, institutionInfos, partyA, institutionNameMap, copyViewerInfoBeans
                    , subjectPrefix, subjectSuffix, fileKey, templateType, institutionNumber, singerList, signDocs, flowNameMap);

//            String signFlowId = signFlows.getString("signFlowId");
//            List<InstitutionBaseInfo> distinctInstitutions = institutionInfos.stream().distinct().collect(Collectors.toList());
//            List<YbFlowInfo> flowInfoList = new ArrayList<>();
//            String singerName = String.join(",", institutionNames);
//            if(StringUtils.isNotBlank(singerName)){
//                singerName.substring(0,MAX_SIGNERSLENGTH);
//            }
//            distinctInstitutions.forEach(institutionInfo -> {
//                YbFlowInfo flowInfo = new YbFlowInfo();
//                flowInfo.setInitiator(initiatorName)
//                        .setNumber(institutionInfo.getNumber())
//                        .setSigners(singerName)
//                        .setSubject(subject)
//                        .setCopyViewers(singerName)
//                        .setSignFlowId(signFlowId)
//                        .setNumber(institutionNumber)
//                        .setAccountType(2)
//                        .setFlowStatus(0)
//                        .setSignStatus("0")
//                        .setFileKey(fileKey)
//                        .setFlowType("Common");
//                flowInfoList.add(flowInfo);
//            });
//            YbFlowInfo flowAInfo = new YbFlowInfo();
//            flowAInfo.setInitiator(initiatorName)
//                    .setNumber(organizeNo)
//                    .setSigners(singerName)
//                    .setSubject(subject)
//                    .setCopyViewers(singerName)
//                    .setSignFlowId(signFlowId)
//                    .setInitiatorTime(new Date())
//                    .setUniqueId(partyA.getUniqueId())
//                    .setAccountType(1)
//                    .setFlowStatus(0)
//                    .setSignStatus("0")
//                    .setFileKey(fileKey)
//                    .setFlowType("Common");
//            flowInfoList.add(flowAInfo);
//            flowInfoService.saveBatch(flowInfoList);
        } else {
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "文档类型不能为空！");
        }
        if (errMsg.length() > 0) {
            return ApiResponse.fail(ErrorCodeEnum.SIGN_ERROR, errMsg.toString());
        } else {
            return new ApiResponse(ErrorCodeEnum.SUCCESS.getCode(), errMsg.toString());
        }

    }

    /**
     * 获取机构信息
     *
     * @param institution
     * @return
     * @throws BizServiceException
     */
    private YbInstitutionInfo getInstitutionInfo(InstitutionBaseInfo institution) throws BizServiceException {
        log.info("获取机构信息请求参数：【{}】", JSON.toJSONString(institution));
        YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(institution.getNumber());
        //获取保险公司机构信息
        if (null == institutionInfo) {
            JSONObject jsonObject = organizationsService.queryOrgans(institution.getOrganizeId(), institution.getNumber());
            // {"errCode":0,"msg":"success","errShow":true,"data":{"organizeId":"f9696842-2d5b-4518-ac40-41f15491dd01","organizeNo":"bx001",
            // "organizeName":"esigntest测试保险公司1","licenseType":12,"licenseNumber":"914403001000123161","legalName":"朱阳",
            // "legalLicenseType":1,"legalLicenseNumber":"330184199401223513","legalMobile":"15957114467","email":null,
            // "legalUniqueId":"330184199401223513","agentUniqueId":"330102199303171815","legalAccountId":"af2ef891-fdab-4f98-b1b0-57226e5f80b7",
            // "agentAccountId":"f338517a-9653-4593-b1f7-3faea7783f22","agentName":"周鸣鸣","status":1,"language":"chinese",
            // "createTime":"2021-08-10 13:09:18","modifyTime":"2021-08-21 15:01:39","esignOrganizeId":"1172d0bd39b14795b903a9d5723fe0a9"}}
            //{"errCode":-1,"msg":"用户不存在","errShow":true,"data":null}
            if ("-1".equals(jsonObject.getString("errCode"))) {
                throw new BizServiceException(jsonObject.getString("msg"));
            }
            institutionInfo = new YbInstitutionInfo();
            institutionInfo.setAccountId(jsonObject.getString("agentAccountId"))
                    .setOrganizeId(institution.getOrganizeId())
                    .setNumber(jsonObject.getString("organizeNo"))
                    .setLegalName(jsonObject.getString("legalName"))
                    .setLegalIdCard(jsonObject.getString("legalLicenseNumber"))
                    .setInstitutionName(jsonObject.getString("organizeName"))
                    .setLegalAccountId(jsonObject.getString("legalAccountId"));
        }
        return institutionInfo;
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
            String flowNam = templateForm.getFormName();
            String flowName = flowNam.substring(0, 2);
            log.info("文本域名称：{}", flowName);
  /*          if (!flowNameInstitutionMap.containsKey(flowName)) {
                throw new BizServiceException("请选择" + flowName + "机构！");
            }*/
            List<InstitutionBaseInfo> institutionInfos = flowNameInstitutionMap.get(flowName);
            log.info("institutionInfos = {}", JSONObject.toJSONString(institutionInfos));
            if (!CollectionUtils.isEmpty(institutionInfos)) {
                if(flowNam.contains("机构编码")){
                    templateFormValueParam.setFormValue(institutionInfos.get(i).getNumber());
                }else if (flowName.equals(maxSizeFlowName)) {
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
    private StandardSignerInfoBean assembleStandardSignerInfoBean(YbInstitutionInfo institutionInfo
            , SingerInfo singerInfo, String fileKey, Map<String, PredefineBean> flowNamePredefineMap
            , ETemplateType templateType, String flowName, boolean addSignTime) throws BizServiceException {
        StandardSignerInfoBean signerInfoBean = new StandardSignerInfoBean();
        //外部机构如果是保险公司则经办人签署，如果是医疗机构则法人签署
        if (institutionInfo.getNumber().startsWith("bx")) {
            signerInfoBean.setAccountId(institutionInfo.getAccountId());
        } else {
            signerInfoBean.setAccountId(institutionInfo.getLegalAccountId());
        }
        signerInfoBean.setAuthorizationOrganizeId(institutionInfo.getOrganizeId());
        signerInfoBean.setAccountType(EAccountType.EXTERNAL.getCode());
        signerInfoBean.setLegalSignFlag(1);
        signerInfoBean.setSignOrder(1);
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
                signInfoBeanV2.setAddSignTime(false);
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
//                    List<SignTimeBean> signTimeBeanList = new ArrayList<>();
                    List<SignInfoBeanV2> signPos = positions.stream().map(position -> {
                        SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
                        //签署方式1-单页签署
                        signInfoBeanV2.setSignType(1);
                        signInfoBeanV2.setAddSignTime(false);
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
                        signInfoBeanV2.setAddSignTime(addSignTime);
                        signInfoBeanV2.setSignDateInfos(getSignTimeBeanList(position.getTemplateSignTimeInfos()));
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
            signInfoBeanV2.setAddSignTime(false);
            signInfoBeanV2.setKey(singerInfo.getKey());
            signInfoBeanV2.setSignType(4);
            signInfoBeanV2.setSignIdentity("ORGANIZE");
            SignInfoBeanV2 legalSignInfoBeanV2 = new SignInfoBeanV2();
            legalSignInfoBeanV2.setKey(singerInfo.getKey() + "法人");
            legalSignInfoBeanV2.setSignType(4);
            legalSignInfoBeanV2.setAddSignTime(addSignTime);
//            legalSignInfoBeanV2.setSignDateInfos(getSignTimeBeanList());
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
    private StandardSignerInfoBean assemblePartyAInfo(Map<String, PredefineBean> flowNamePredefineMap, int partyASignType
            , String fileKey, String organizeNo, ETemplateType templateType, boolean addSignTime) throws Exception {
        StandardSignerInfoBean partyA = new StandardSignerInfoBean();
        partyA.setSignOrder(2);
//        partyA.setLegalSignFlag(1);
        log.info("发起方（甲方）机构编码：{}", organizeNo);
        JSONObject innerOrgans = organizationsService.queryInnerOrgans(organizeNo);
        log.info("甲方机构信息：{}", innerOrgans.toJSONString());
        QueryInnerAccountsReq queryInnerAccountsReq = new QueryInnerAccountsReq();
        String organizeId = innerOrgans.getString("organizeId");
        partyA.setAuthorizationOrganizeId(organizeId);
        partyA.setOrganizeName(innerOrgans.getString("organizeName"));
        queryInnerAccountsReq.setOrganizeId(organizeId);
        queryInnerAccountsReq.setPageSize("10");
        queryInnerAccountsReq.setPageIndex("1");
        // 填充印章信息
        JSONObject innerOrgansSeals = signedService.getInnerOrgansSeals(organizeId, organizeNo);
        log.info("甲方印章信息：{}", innerOrgansSeals);
        if (innerOrgansSeals.containsKey("errCode")) {
            throw new BizServiceException(innerOrgansSeals.getString("msg"));
        }
        String innerOrgansSealsStr = innerOrgansSeals.getString("seals");
        List<Seal> sealList = JSON.parseArray(innerOrgansSealsStr, Seal.class);
        Map<Integer, String> sealTypeAndSealIdMap = sealList.stream().collect(Collectors.toMap(Seal::getSubSealTypeId, Seal::getSealId));
        JSONObject sealInfos = signedService.getSealInfos(sealTypeAndSealIdMap.get(1));
        log.info("印章管理员信息：{}", sealInfos);
        if (innerOrgansSeals.containsKey("errCode")) {
            throw new BizServiceException(innerOrgansSeals.getString("msg"));
        }
        String sealUsers = sealInfos.getString("sealUsers");
        List<SealUser> sealUserList = JSON.parseArray(sealUsers, SealUser.class);
        SealUser sealUser = CollectionUtils.firstElement(sealUserList);
        if (sealUser != null) {
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
                signInfoBeanV2.setAddSignTime(false);
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
                        signInfoBeanV2.setAddSignTime(false);
                        signInfoBeanV2.setPosX(Float.valueOf(position.getPosX()));
                        signInfoBeanV2.setPosY(Float.valueOf(position.getPosY()));
                        signInfoBeanV2.setPosPage(position.getPageNo());
//                        signInfoBeanV2.setSignIdentity("ORGANIZE");
                        signInfoBeanV2.setSealId(sealTypeAndSealIdMap.get(1));
//                        signInfoBeanV2.setAddSignTime(addSignTime);
                        return signInfoBeanV2;
                    }).collect(Collectors.toList());
                    List<SignInfoBeanV2> legalSignPos = legalPositions.stream().map(position -> {
                        SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
                        //签署方式1-单页签署
                        signInfoBeanV2.setSignType(1);
                        signInfoBeanV2.setAddSignTime(false);
                        signInfoBeanV2.setPosX(Float.valueOf(position.getPosX()));
                        signInfoBeanV2.setPosY(Float.valueOf(position.getPosY()));
                        signInfoBeanV2.setPosPage(position.getPageNo());
//                        signInfoBeanV2.setSignIdentity("LEGAL");
                        signInfoBeanV2.setSealId(sealTypeAndSealIdMap.get(3));
                        //todo 法人才需要日期
                        signInfoBeanV2.setAddSignTime(addSignTime);
                        signInfoBeanV2.setSignDateInfos(getSignTimeBeanList(position.getTemplateSignTimeInfos()));
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
        } else {
            //文件直传发起
            List<SignInfoBeanV2> signPos = new ArrayList<>();
            SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
            signInfoBeanV2.setKey("甲方");
            signInfoBeanV2.setAddSignTime(false);
            signInfoBeanV2.setSignType(4);
            signInfoBeanV2.setSignIdentity("ORGANIZE");
            SignInfoBeanV2 legalSignInfoBeanV2 = new SignInfoBeanV2();
            legalSignInfoBeanV2.setKey("甲方法人");
            legalSignInfoBeanV2.setSignType(4);
            //todo 法人才需要日期
            legalSignInfoBeanV2.setAddSignTime(addSignTime);
//            legalSignInfoBeanV2.setSignDateInfos();
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
     *
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

    //创建流程出参处理
    public ApiResponse handleCreateSign(StringBuilder errMsg, List<InstitutionBaseInfo> institutionInfos
            , StandardSignerInfoBean partyA, Map<String, String> institutionNameMap, List<CopyViewerInfoBean> copyViewerInfoBeans
            , String subjectPrefix, String subjectSuffix, String fileKey, ETemplateType templateType
            , String institutionNumber, List<StandardSignerInfoBean> singerList
            , List<FlowDocBean> signDocs, Map<String, String> flowNameMap) {

        StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
        String initiatorName = partyA.getAccountName();
        standardCreateFlow.setInitiatorName(initiatorName);
        standardCreateFlow.setCopyViewers(copyViewerInfoBeans);
        //手机号或者邮箱
        standardCreateFlow.setInitiatorMobile(partyA.getContactMobile());
        standardCreateFlow.setInitiatorAccountId(partyA.getAccountId());
        standardCreateFlow.setSigners(singerList);
        standardCreateFlow.setSignDocs(signDocs);
        String subject = subjectPrefix + "-" + DateUtil.getNowTimestampStr();
        //流程主题
        Date now = new Date();
        standardCreateFlow.setSubject(subject);
        JSONObject signFlows = signedService.createSignFlows(standardCreateFlow);
        log.info("7.创建流程出参：{}", JSON.toJSONString(signFlows));
        String BatchStatus = Cons.BatchStr.BATCH_STATUS_SUCCESS;
        if (signFlows.containsKey("errCode")) {
            BatchStatus = Cons.BatchStr.BATCH_STATUS_FAIL;
//            StringBuilder sb = new StringBuilder();
//            singerList.forEach(s ->{
//                sb.append(s.getAuthorizationOrganizeId()).append(",");
//            });
//            sb.deleteCharAt(sb.length() - 1);
            errMsg.append(" 创建签署流程失败，签署文档名:").append(subject).append("，E签宝失败提示:").append(signFlows.getString("msg"));
//            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), signFlows.getString("msg"));
        }

        String signFlowIdTemp = signFlows.getString("signFlowId");
        if (StringUtils.isBlank(signFlowIdTemp) && Cons.BatchStr.BATCH_STATUS_FAIL.equals(BatchStatus)) {
            signFlowIdTemp = "5x00";
        }
        String signFlowId = signFlowIdTemp;
        List<InstitutionBaseInfo> distinctInstitutions = institutionInfos.stream().distinct().collect(Collectors.toList());
        List<YbFlowInfo> flowInfoList = new ArrayList<>();
//        String singerName = String.join(",", institutionNames);
        String singerName = institutionNameMap.values().stream().collect(Collectors.joining(","));
        log.info("partyA={}", JSONObject.toJSONString(partyA));
        log.info("institutionNameMap={}", JSONObject.toJSONString(institutionNameMap));
        log.info("singerName={}", singerName);
        if (StringUtils.isNotBlank(singerName) && singerName.length() > MAX_SIGNERSLENGTH) {
            singerName = singerName.substring(0, MAX_SIGNERSLENGTH);
            singerName = singerName + "……";
        }
        final String sn = singerName;
        final String BatchStatus2 = BatchStatus;
        String batchNo = subjectPrefix + subjectSuffix;
        distinctInstitutions.forEach(institutionInfo -> {
            YbFlowInfo flowInfo = new YbFlowInfo();
            flowInfo.setInitiator(initiatorName)
                    .setNumber(institutionInfo.getNumber())
                    .setSigners(institutionNameMap.get(institutionInfo.getNumber()))
                    .setSubject(subject)
                    .setCopyViewers(sn)
                    .setSignFlowId(signFlowId)
                    .setAccountType(2)
                    .setFlowStatus(0)
//                    .setFileKey(fileKey)
                    .setSignStatus("0")
                    .setFlowType("Common")
                    .setFlowName(flowNameMap.get(institutionInfo.getNumber()))
                    .setBatchStatus(BatchStatus2)
                    .setBatchNo(batchNo);
            if (StringUtils.isBlank(fileKey)) {
                flowInfo.setFileKey("文档为空");
            } else {
                flowInfo.setFileKey(fileKey);
            }
            if (ETemplateType.TEMPLATE_FILL == templateType) {
                flowInfo.setInitiatorTime(now);
            } else if (ETemplateType.FILE_UPLOAD == templateType) {
                flowInfo.setNumber(institutionNumber);
            }
            flowInfoList.add(flowInfo);
        });

        //甲方
        YbFlowInfo flowAInfo = new YbFlowInfo();
        flowAInfo.setInitiator(initiatorName)
                .setNumber(partyA.getOrganizeNo())
                .setSigners(partyA.getOrganizeName())
                .setSubject(subject)
                .setCopyViewers(sn)
                .setSignFlowId(signFlowId)
                .setInitiatorTime(now)
                .setUniqueId(partyA.getUniqueId())
                .setAccountType(1)
                .setFlowStatus(0)
                .setSignStatus("0")
                .setFlowType("Common")
                .setFlowName(flowNameMap.get(partyA.getOrganizeNo()))
                .setBatchStatus(BatchStatus2)
                .setBatchNo(batchNo)
        ;

        if (StringUtils.isBlank(flowAInfo.getSigners())) {
            flowAInfo.setSigners(sn);
        }
        if (StringUtils.isBlank(fileKey)) {
            flowAInfo.setFileKey("文档为空");
        } else {
            flowAInfo.setFileKey(fileKey);
        }
        if (ETemplateType.TEMPLATE_FILL == templateType) {
            flowAInfo.setInitiatorTime(now);
        } else if (ETemplateType.FILE_UPLOAD == templateType) {
            flowAInfo.setInitiatorTime(new Date());
        }

        flowInfoList.add(flowAInfo);
        flowInfoService.saveBatch(flowInfoList);

        if (Cons.BatchStr.BATCH_STATUS_SUCCESS.equals(BatchStatus2)) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(signFlows.getString("errCode"), errMsg.toString());
        }


    }

    /**
     * 每次都新建对象，为解决JSONObject.toString()引起的循环对对象变$ref问题
     * 使用JSONObject.toString(param,DisableCircularReferenceDetect)会引起签名错误
     * @return
     */
    public List<SignTimeBean> getSignTimeBeanList(List<TemplateSignTimeInfo> timeInfoList){
        List<SignTimeBean> list = new ArrayList<>();
        timeInfoList.forEach(tf->{
            SignTimeBean signTimeBean = new SignTimeBean();
            BeanUtils.copyProperties(tf,signTimeBean);
            list.add(signTimeBean);
        });
        return list;
    }

}
