package com.hfi.insurance.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ESignType;
import com.hfi.insurance.mapper.YbFlowInfoMapper;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.YbOrgTd;
import com.hfi.insurance.model.sign.TemplateFlowBean;
import com.hfi.insurance.model.sign.req.CreateSignFlowReq;
import com.hfi.insurance.model.sign.req.FlowDocBean;
import com.hfi.insurance.model.sign.req.SignInfoBeanV2;
import com.hfi.insurance.model.sign.req.StandardCreateFlowBO;
import com.hfi.insurance.model.sign.req.StandardSignDocBean;
import com.hfi.insurance.model.sign.req.StandardSignerInfoBean;
import com.hfi.insurance.service.IYbOrgTdService;
import com.hfi.insurance.service.SignedBizService;
import com.hfi.insurance.service.SignedService;
import com.hfi.insurance.utils.EnumHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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
    @Override
    public ApiResponse createSignFlow(CreateSignFlowReq req) {
        String templateId = req.getTemplateId();
        JSONObject templateInfo = signedService.getTemplateInfo(templateId);
        StandardCreateFlowBO standardCreateFlow = new StandardCreateFlowBO();
        List<FlowDocBean> signDocs = new ArrayList<>();
        List<StandardSignerInfoBean> signers = new ArrayList<>();
        FlowDocBean flowDocBean = new FlowDocBean();
        String fileKey = templateInfo.getString("fileKey");
        flowDocBean.setDocFilekey(fileKey);
        flowDocBean.setDocName(templateInfo.getString("templateName"));
        signDocs.add(flowDocBean);
        standardCreateFlow.setSignDocs(signDocs);
        req.getSingerInfos().stream().map(singerInfo -> {
            StandardSignerInfoBean signerInfoBean  = new StandardSignerInfoBean();
            ESignType signType = EnumHelper.translate(ESignType.class, singerInfo.getSignType());
            if (ESignType.DEFAULT_COORDINATE_SIGN == signType || ESignType.DEFAULT_KEY_WORD_SIGN == signType){
                signerInfoBean.setAutoSign(true);
            }
            List<StandardSignDocBean> signDocDetails = new ArrayList<>();
            StandardSignDocBean standardSignDocBean = new StandardSignDocBean();
            standardSignDocBean.setDocFilekey(fileKey);
            List<SignInfoBeanV2> signPos = new ArrayList<>();
            SignInfoBeanV2 signInfoBean = new SignInfoBeanV2();
            signInfoBean.setKey(singerInfo.getKey());
            String templateFlows = templateInfo.getString("templateFlows");
            List<TemplateFlowBean> templateFlowBeans = JSON.parseArray(templateFlows, TemplateFlowBean.class);
//            signInfoBean.setPosX();
            signPos.add(signInfoBean);
//            signPos.add(templateInfo.getK)
            standardSignDocBean.setSignPos(signPos);
            signDocDetails.add(standardSignDocBean);
            signerInfoBean.setSignDocDetails(signDocDetails);
            return signerInfoBean;
        }).collect(Collectors.toList());

        standardCreateFlow.setSigners(signers);
        log.info("创建流程入参：{}", JSON.toJSONString(standardCreateFlow));
        JSONObject signFlows = signedService.createSignFlows(standardCreateFlow);
        String signFlowId = signFlows.getString("signFlowId");
        YbFlowInfo flowInfo = new YbFlowInfo();
        flowInfo.setSignFlowId(signFlowId);
        flowInfoMapper.insert(flowInfo);
        return null;
    }
}
