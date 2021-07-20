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
import com.hfi.insurance.model.sign.TemplateFlowBean;
import com.hfi.insurance.model.sign.TemplateFormBean;
import com.hfi.insurance.model.sign.TemplateInfoBean;
import com.hfi.insurance.model.sign.req.CreateSignFlowReq;
import com.hfi.insurance.model.sign.req.FlowDocBean;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionReq;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionV2Model;
import com.hfi.insurance.model.sign.req.SignInfoBeanV2;
import com.hfi.insurance.model.sign.req.SingerInfo;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @Resource
    private Cache<String, String> caffeineCache;

    @Override
    public ApiResponse createSignFlow(CreateSignFlowReq req) {
        List<SingerInfo> singerInfos = req.getSingerInfos();
        Map<String, List<InstitutionInfo>> flowNameInstitutionMap = new HashMap<>();
        Map<Integer, String> flowNameSizeMap = new LinkedHashMap<>(16);
        List<String> institutionNames = new ArrayList<>();
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
        for (int i = 0; i < maxSize; i++) {
            StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
            //文档信息
            List<FlowDocBean> signDocs = new ArrayList<>();
            FlowDocBean flowDocBean = new FlowDocBean();
            ETemplateType templateType = EnumHelper.translate(ETemplateType.class, req.getTemplateType());
            if (ETemplateType.TEMPLATE_FILL == templateType) {
                //填充模板的形式发起签署
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
                    if ("甲方".equals(flowName)){
                        templateFormValueParam.setFormValue("杭州市医疗保障局");
                    }
                    templateFormValues.add(templateFormValueParam);
                }
                templateUseParam.setTemplateFormValues(templateFormValues);
                templateUseParam.setTemplateId(templateId);
                JSONObject jsonObject = signedService.buildTemplateDoc(templateUseParam);
                log.info("填充模板后文档信息：{}",jsonObject);
                String fileKey = jsonObject.getString("fileKey");
                flowDocBean.setDocFilekey(fileKey);
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
                    if (signatory != null) {
                        accountSignatoryMap.put(signatory.getAccountId(), templateFlowBean.getPredefine());
                    }
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
                    if (singerInfo.getFlowName().equals(maxSizeFlowName)) {
                        institution = institutionInfoList.get(i);
                    } else {
                        institution = CollectionUtils.firstElement(institutionInfoList);
                    }
                    String accountId = "";
                    if (institution != null) {
                        YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(institution.getNumber());
                        accountId = institutionInfo.getAccountId();
                    }
                    PredefineBean predefineBean = accountSignatoryMap.get(accountId);
                    if (predefineBean == null) {
                        predefineBean = flowNamePredefineMap.get(singerInfo.getFlowName());
                    }
                    log.info("位置信息：{}", JSON.toJSONString(predefineBean));
                    //填充签署人信息
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
                    //签署方式默认为0
                    signInfoBeanV2.setSignType(1);
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
                    singerList.add(signerInfoBean);
                }
                //todo 填充甲方信息
                StandardSignerInfoBean partyA = new StandardSignerInfoBean();
                String areaCode = caffeineCache.asMap().get("areaCode");
                //String orgName = getInstitutionNameByAreaCode(areaCode);

                //todo 查询内部机构详情 /V1/organizations/innerOrgans/queryByOrgname
                partyA.setAccountId("279e974f-577d-47fa-86cd-6672c617043a");
//                partyA.setAuthorizationOrganizeId();
                partyA.setAccountType(1);
                PredefineBean predefineBean = accountSignatoryMap.get("279e974f-577d-47fa-86cd-6672c617043a");
                if (null == predefineBean) {
                    predefineBean = flowNamePredefineMap.get("甲方");
                }
                ESignType signType = EnumHelper.translate(ESignType.class, req.getPartyASignType());
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
                    String keyWord = predefineBean.getKeyWord();
                    //todo 甲方关键词
                    signInfoBeanV2.setKey(keyWord);
                }
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
                singerList.add(partyA);
                //"发起人姓名不能为空
                standardCreateFlow.setInitiatorName("超级管理员");
                //手机号或者邮箱
                standardCreateFlow.setInitiatorMobile("18879476719");
                standardCreateFlow.setSigners(singerList);
                //流程主题
                standardCreateFlow.setSubject(req.getTemplateId() + "-" +System.currentTimeMillis());
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
                    standardSignDocBean.setDocFilekey(req.getFileKey());
                    List<SignInfoBeanV2> signPos = new ArrayList<>();
                    SignInfoBeanV2 signInfoBean = new SignInfoBeanV2();
                    //签署方式默认为0
                    signInfoBean.setSignType(1);
                    signInfoBean.setKey(singerInfo.getKey());
                    signPos.add(signInfoBean);
                    standardSignDocBean.setSignPos(signPos);
                    signDocDetails.add(standardSignDocBean);
                    signerInfoBean.setSignDocDetails(signDocDetails);
                    return signerInfoBean;
                }).collect(Collectors.toList());
                standardCreateFlow.setSigners(singerList);
                standardCreateFlow.setSubject("");
            }
            log.info("创建流程入参：{}", JSON.toJSONString(standardCreateFlow));
            JSONObject signFlows = signedService.createSignFlows(standardCreateFlow);
            log.info("创建流程出参：{}", JSON.toJSONString(signFlows));
            // todo 优化
            Integer errCode = signFlows.getInteger("errCode");
            if (errCode != null && -1 == errCode){
                return new ApiResponse(signFlows.getString("msg"));
            }

            String signFlowId = signFlows.getString("signFlowId");
            YbFlowInfo flowInfo = new YbFlowInfo();
            flowInfo.setInitiator("超级管理员");
            String singerName = String.join(",", institutionNames);
            flowInfo.setSigners(singerName);
            flowInfo.setSubject(req.getTemplateId() + "-" +System.currentTimeMillis());
            flowInfo.setCopyViewers(singerName);
            flowInfo.setSignFlowId(signFlowId);
            flowInfoMapper.insert(flowInfo);
        }
        return new ApiResponse(ErrorCodeEnum.SUCCESS);
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
        log.info("获取模板详细信息响应参数：{}", templateInfo);
        if (templateInfo.getBoolean("success")) {
            String templateInfoString = templateInfo.getString("template");
            JSONObject data = JSON.parseObject(templateInfoString);
            return new ApiResponse(data);
        } else {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR);
        }
    }
}
