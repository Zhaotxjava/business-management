package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ESignType;
import com.hfi.insurance.enums.ETemplateType;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.mapper.YbFlowInfoMapper;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.sign.Position;
import com.hfi.insurance.model.sign.PredefineBean;
import com.hfi.insurance.model.sign.SignatoryBean;
import com.hfi.insurance.model.sign.StandardDepartment;
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
import com.hfi.insurance.model.sign.res.StandardAccountListReturn;
import com.hfi.insurance.service.IYbFlowInfoService;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.IYbOrgTdService;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.service.SignedBizService;
import com.hfi.insurance.service.SignedService;
import com.hfi.insurance.utils.EnumHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
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

//    @Resource
//    private Cache<String, String> caffeineCache;

    @Override
    public ApiResponse createSignFlow(CreateSignFlowReq req, HttpSession session) {
        String organizeNo = (String) session.getAttribute("areaCode");
        String institutionNumber = (String) session.getAttribute("number");
        ETemplateType templateType = EnumHelper.translate(ETemplateType.class, req.getTemplateType());
        //填充模板的形式发起签署
        if (ETemplateType.TEMPLATE_FILL == templateType) {
            List<SingerInfo> singerInfos = req.getSingerInfos();
            Map<String, List<InstitutionInfo>> flowNameInstitutionMap = new HashMap<>();
            Map<Integer, String> flowNameSizeMap = new LinkedHashMap<>(16);
            List<String> institutionNames = new ArrayList<>();
            List<InstitutionInfo> institutionInfos = new ArrayList<>();
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
            //乙方（3）* （丙方+丁方）
            for (int i = 0; i < maxSize; i++) {
                StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
                //文档信息
                List<FlowDocBean> signDocs = new ArrayList<>();
                FlowDocBean flowDocBean = assembleSignDocs(req, templateInfo, flowNameInstitutionMap, maxSizeFlowName, i);
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
                    List<InstitutionInfo> institutionInfoList = singerInfo.getInstitutionInfoList();
                    //获取签署机构名称
                    String singerNames = institutionInfoList.stream().map(InstitutionInfo::getInstitutionName)
                            .collect(Collectors.joining(","));
                    institutionNames.add(singerNames);
                    InstitutionInfo institution = null;
                    String flowName = singerInfo.getFlowName();
                    if (flowName.equals(maxSizeFlowName)) {
                        institution = institutionInfoList.get(i);
                    } else {
                        institution = CollectionUtils.firstElement(institutionInfoList);
                    }
                    String accountId = "";
                    if (institution != null) {
                        institutionInfos.add(institution);
                        YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(institution.getNumber());
                        if (institutionInfo != null){
                            accountId = institutionInfo.getAccountId();
                        }
                    }
                    PredefineBean predefineBean = flowNamePredefineMap.get(flowName);
                    log.info("位置信息：{}", JSON.toJSONString(predefineBean));
                    //填充签署人信息
                    StandardSignerInfoBean signerInfoBean = assembleStandardSignerInfoBean(accountId, singerInfo, fileKey, predefineBean,templateType);
                    singerList.add(signerInfoBean);
                }
                //todo 填充甲方信息
                PredefineBean predefineBeanA = flowNamePredefineMap.get("甲方");
                StandardSignerInfoBean partyA = null;
                try {
                    partyA = assemblePartyAInfo(predefineBeanA, req.getPartyASignType(), fileKey, organizeNo,templateType);
                } catch (Exception e) {
                    log.error("填充甲方信息失败：{}",e.getMessage());
                    e.printStackTrace();
                    return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR);
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
                // todo 优化
                Integer errCode = signFlows.getInteger("errCode");
                if (errCode != null && -1 == errCode) {
                    return new ApiResponse(signFlows.getString("msg"));
                }
                String signFlowId = signFlows.getString("signFlowId");
                List<InstitutionInfo> distinctInstitutions = institutionInfos.stream().distinct().collect(Collectors.toList());
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
            }
        } else if (ETemplateType.FILE_UPLOAD == templateType) {
            List<String> institutionNames = new ArrayList<>();
            List<InstitutionInfo> institutionInfos = new ArrayList<>();
            StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
            //文档信息
            List<FlowDocBean> signDocs = new ArrayList<>();
            FlowDocBean flowDocBean = new FlowDocBean();
            String fileKey = req.getFileKey();
            flowDocBean.setDocFilekey(fileKey);
            signDocs.add(flowDocBean);
            standardCreateFlow.setSignDocs(signDocs);
            List<StandardSignerInfoBean> singerList = new ArrayList<>();
            //填充乙丙丁方签署信息
            for (SingerInfo singerInfo : req.getSingerInfos()) {
                List<InstitutionInfo> institutionInfoList = singerInfo.getInstitutionInfoList();
                //获取签署机构名称
                String singerNames = institutionInfoList.stream().map(InstitutionInfo::getInstitutionName)
                        .collect(Collectors.joining(","));
                institutionNames.add(singerNames);
                InstitutionInfo institution = null;
                String flowName = singerInfo.getFlowName();

                institution = CollectionUtils.firstElement(institutionInfoList);

                String accountId = "";
                if (institution != null) {
                    institutionInfos.add(institution);
                    YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(institution.getNumber());
                    accountId = institutionInfo.getAccountId();
                }
                //填充签署人信息
                StandardSignerInfoBean signerInfoBean = assembleStandardSignerInfoBean(accountId, singerInfo, fileKey, null, templateType);
                singerList.add(signerInfoBean);
            }
            StandardSignerInfoBean partyA = null;
            try {
                partyA = assemblePartyAInfo(null, req.getPartyASignType(), fileKey, organizeNo,templateType);
            } catch (Exception e) {
                log.error("填充甲方信息失败：{}",e.getMessage());
                e.printStackTrace();
                return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR);
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
            // todo 优化
            Integer errCode = signFlows.getInteger("errCode");
            if (errCode != null && -1 == errCode) {
                return new ApiResponse(signFlows.getString("msg"));
            }
            String signFlowId = signFlows.getString("signFlowId");
            List<InstitutionInfo> distinctInstitutions = institutionInfos.stream().distinct().collect(Collectors.toList());
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
            return new ApiResponse("文档类型不能为空！");
        }
        return new ApiResponse(ErrorCodeEnum.SUCCESS);
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
                                         Map<String, List<InstitutionInfo>> flowNameInstitutionMap,
                                         String maxSizeFlowName, int i) {
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
            List<InstitutionInfo> institutionInfos = flowNameInstitutionMap.get(flowName);

            if (!CollectionUtils.isEmpty(institutionInfos)) {
                if (flowName.equals(maxSizeFlowName)) {
                    templateFormValueParam.setFormValue(institutionInfos.get(i).getInstitutionName());
                } else {
                    templateFormValueParam.setFormValue(institutionInfos.get(0).getInstitutionName());
                }
            }
            if ("甲方".equals(flowName)) {
                templateFormValueParam.setFormValue("杭州市医疗保障局");
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
     * @param accountId
     * @param singerInfo
     * @param fileKey
     * @param predefineBean
     * @return
     */
    private StandardSignerInfoBean assembleStandardSignerInfoBean(String accountId, SingerInfo singerInfo, String fileKey, PredefineBean predefineBean,ETemplateType templateType) {
        StandardSignerInfoBean signerInfoBean = new StandardSignerInfoBean();
        signerInfoBean.setAccountId(accountId);
//                    signerInfoBean.setAuthorizationOrganizeId("");
        signerInfoBean.setAccountType(singerInfo.getAccountType());
        ESignType signType = EnumHelper.translate(ESignType.class, singerInfo.getSignType());
        if (ESignType.DEFAULT_COORDINATE_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
            signerInfoBean.setAutoSign(false);
        }
        //签署文档信息;指定文档进行签署，未指定的文档将作为只读
        List<StandardSignDocBean> signDocDetails = new ArrayList<>();
        StandardSignDocBean standardSignDocBean = new StandardSignDocBean();
        standardSignDocBean.setDocFilekey(fileKey);

        List<SignInfoBeanV2> signPos = new ArrayList<>();
        SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
        //签署方式1-单页签署
        signInfoBeanV2.setSignType(1);
        if (ETemplateType.TEMPLATE_FILL == templateType){
            if (ESignType.MANUAL_KEY_WORD_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
                String keyWord = predefineBean.getKeyWord();
                signInfoBeanV2.setKey(keyWord != null ? keyWord : singerInfo.getKey());
            }
            //位置签署要匹配区域
            if ((ESignType.MANUAL_COORDINATE_SIGN == signType || ESignType.DEFAULT_COORDINATE_SIGN == signType)) {
                List<Position> positions = predefineBean.getPositions();
                Position position = CollectionUtils.firstElement(positions);
                signInfoBeanV2.setPosX(Float.valueOf(position.getPosX()));
                signInfoBeanV2.setPosY(Float.valueOf(position.getPosY()));
                signInfoBeanV2.setPosPage(position.getPageNo());
            }
            signPos.add(signInfoBeanV2);
            standardSignDocBean.setSignPos(signPos);
            signDocDetails.add(standardSignDocBean);
            signerInfoBean.setSignDocDetails(signDocDetails);
            return signerInfoBean;
        }else {
            signInfoBeanV2.setKey(singerInfo.getKey());
            signPos.add(signInfoBeanV2);
            standardSignDocBean.setSignPos(signPos);
            signDocDetails.add(standardSignDocBean);
            signerInfoBean.setSignDocDetails(signDocDetails);
            return signerInfoBean;
        }
    }


    /**
     * 填充甲方信息
     *
     * @param predefineBean
     * @param partyASignType
     * @param fileKey
     * @return
     */
    private StandardSignerInfoBean assemblePartyAInfo(PredefineBean predefineBean, int partyASignType, String fileKey, String organizeNo,ETemplateType templateType) throws Exception{
        StandardSignerInfoBean partyA = new StandardSignerInfoBean();
        log.info("发起方（甲方）机构编码：{}", organizeNo);
        JSONObject innerOrgans = organizationsService.queryInnerOrgans(organizeNo);
        log.info("甲方机构信息：{}",innerOrgans.toJSONString());
        QueryInnerAccountsReq queryInnerAccountsReq = new QueryInnerAccountsReq();
        queryInnerAccountsReq.setOrganizeId(innerOrgans.getString("organizeId"));
        queryInnerAccountsReq.setPageSize("10");
        queryInnerAccountsReq.setPageIndex("1");
        JSONObject innerAccounts = organizationsService.queryInnerAccounts(queryInnerAccountsReq);
        String accounts = innerAccounts.getString("accounts");
        log.info("甲方用户信息：{}",accounts);
        if (null != accounts) {
            List<StandardAccountListReturn> accountListReturns = JSON.parseArray(accounts, StandardAccountListReturn.class);
            StandardAccountListReturn accountReturn = CollectionUtils.firstElement(accountListReturns);
            log.info("甲方用户id:{}", accountReturn.getAccountId());
            //partyA.setAccountId("279e974f-577d-47fa-86cd-6672c617043a");
            partyA.setAccountId(accountReturn.getAccountId());
//            partyA.setAuthorizationOrganizeId();
            partyA.setContactMobile(accountReturn.getMobile());
            partyA.setAccountName(accountReturn.getName());
            partyA.setUniqueId(accountReturn.getUniqueId());
            partyA.setAccountId(accountReturn.getAccountId());
        }
        partyA.setAccountType(1);
        ESignType signType = EnumHelper.translate(ESignType.class, partyASignType);
        if (ESignType.DEFAULT_COORDINATE_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
            partyA.setAutoSign(false);
        }
        //签署文档信息;指定文档进行签署，未指定的文档将作为只读
        List<StandardSignDocBean> signDocDetails = new ArrayList<>();
        StandardSignDocBean standardSignDocBean = new StandardSignDocBean();
        standardSignDocBean.setDocFilekey(fileKey);

        List<SignInfoBeanV2> signPos = new ArrayList<>();
        SignInfoBeanV2 signInfoBeanV2 = new SignInfoBeanV2();
        //签署方式默认为0
        signInfoBeanV2.setSignType(1);
        if (ESignType.MANUAL_KEY_WORD_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType) {
            //String keyWord = predefineBean.getKeyWord();
            //todo 甲方关键词
            signInfoBeanV2.setKey("甲方");
        }
        if (ETemplateType.TEMPLATE_FILL == templateType){
            //位置签署要匹配区域
            if (ESignType.MANUAL_COORDINATE_SIGN == signType || ESignType.DEFAULT_COORDINATE_SIGN == signType) {
                List<Position> positions = predefineBean.getPositions();
                Position position = CollectionUtils.firstElement(positions);
                signInfoBeanV2.setPosX(Float.valueOf(position.getPosX()));
                signInfoBeanV2.setPosY(Float.valueOf(position.getPosY()));
                signInfoBeanV2.setPosPage(position.getPageNo());
            }
            signPos.add(signInfoBeanV2);
            standardSignDocBean.setSignPos(signPos);
            signDocDetails.add(standardSignDocBean);
            partyA.setSignDocDetails(signDocDetails);
        }
        return partyA;
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
    public ApiResponse getPageWithPermission(GetPageWithPermissionReq req) {
        GetPageWithPermissionV2Model getPageWithPermissionV2Model = new GetPageWithPermissionV2Model();
        BeanUtils.copyProperties(req, getPageWithPermissionV2Model);
        getPageWithPermissionV2Model.setPageIndex(req.getPageNum());
        JSONObject result = signedService.getPageWithPermission(getPageWithPermissionV2Model);
        log.info("获取模板列表响应参数：{}", result);
        if (result.getBoolean("success")) {
            String templateInfos = result.getString("templateInfos");
            Page page = new Page();
            page.setTotal(result.getInteger("totalCount"));
            JSONArray data = JSON.parseArray(templateInfos);
            page.setRecords(data);
            return new ApiResponse(page);
        } else {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR);
        }
    }

    @Override
    public ApiResponse getTemplateInfo(String templateId) {
        JSONObject templateInfo = signedService.getTemplateInfo(templateId);
        Map<String,Integer> res = new HashMap<>();
        log.info("获取模板详细信息响应参数：{}", templateInfo);
        if (templateInfo.getBoolean("success")) {
            String templateInfoString = templateInfo.getString("template");
            TemplateInfoBean templateInfoBean = JSON.parseObject(templateInfoString, TemplateInfoBean.class);
            List<TemplateFlowBean> templateFlows = templateInfoBean.getTemplateFlows();
            List<PredefineBean> predefineList = templateFlows.stream().map(TemplateFlowBean::getPredefine).collect(Collectors.toList());
            Optional<PredefineBean> any = predefineList.stream().filter(predefineBean -> null != predefineBean.getPositions()).findAny();
            if (any.isPresent()) {
                //有坐标
                res.put("flag",1);
                return new ApiResponse(res);
            } else {
                //无坐标
                res.put("flag",0);
                return new ApiResponse(res);
            }
        } else {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR);
        }
    }
}
