package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfi.insurance.enums.BatchQueryTypeEnum;
import com.hfi.insurance.enums.Cons;
import com.hfi.insurance.mapper.YbFlowInfoMapper;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.sign.req.GetRecordInfoBatchReq;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.service.IYbFlowInfoService;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 签署流程记录 服务实现类
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-19
 */
@Service
@Slf4j
public class YbFlowInfoServiceImpl extends ServiceImpl<YbFlowInfoMapper, YbFlowInfo> implements IYbFlowInfoService {

    @Autowired
    private IYbInstitutionInfoService iYbInstitutionInfoService;

    @Override
    public Page<YbFlowInfo> getSignedRecord(String institutionNumber, GetRecordInfoReq req) {
        Page<YbFlowInfo> page = new Page<>(req.getPageNum(), req.getPageSize());
        QueryWrapper<YbFlowInfo> queryWrapper = pkQueryWrapper(institutionNumber, req);
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<String> getSignedRecord(String institutionNumber, GetRecordInfoBatchReq req) {
        log.info("getSignedRecord input institutionNumber = {},req = {}",institutionNumber,JSONObject.toJSONString(req));
//        Page<YbFlowInfo> page = new Page<>(req.getPageNum(), req.getPageSize());
        QueryWrapper<YbFlowInfo> queryWrapper = pkQueryWrapperByBatch(institutionNumber, req);
        List<YbFlowInfo> list = baseMapper.selectList(queryWrapper);
        List<String> result = new ArrayList<>();
        list.forEach( y ->{
            result.add(y.getSignFlowId());
        });
        return result;
    }

    @Override
    public List<YbFlowInfo> getSignedRecordByAreaCode(String institutionNumber) {
        QueryWrapper<YbFlowInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct sign_flow_id").likeRight("number", institutionNumber);
        queryWrapper.eq("flow_status", "2");
        queryWrapper.and(i -> i.isNull("batch_status").or().eq("batch_status", Cons.BatchStr.BATCH_STATUS_SUCCESS));
        queryWrapper.orderByDesc("initiator_time");
        List<YbFlowInfo> list = baseMapper.selectList(queryWrapper);
        log.info("getSignedRecordByAreaCode result list = {}",JSONObject.toJSONString(list));
        return list;
    }

    @Override
    public Page<YbFlowInfo> getSignedRecordByBatchDownload(String institutionNumber, GetRecordInfoReq req) {
        return null;
    }

    @Override
    public Integer getSignedRecordCount(String institutionNumber, GetRecordInfoReq req) {
        QueryWrapper<YbFlowInfo> queryWrapper = pkQueryWrapper(institutionNumber, req);
        return baseMapper.selectCount(queryWrapper);
    }

    public QueryWrapper<YbFlowInfo> pkQueryWrapper(String institutionNumber, GetRecordInfoReq req) {
        QueryWrapper<YbFlowInfo> queryWrapper = new QueryWrapper<>();
        //SqlUtils---concatLike
        queryWrapper.likeRight("number", institutionNumber);
        if (StringUtils.isNotBlank(req.getSubject())) {
            queryWrapper.like("subject", req.getSubject());
        }
        if (StringUtils.isNotBlank(req.getSignStatus())) {
            queryWrapper.eq("sign_status", req.getSignStatus());
        }
        if (null != req.getSignFlowId()) {
            queryWrapper.eq("sign_flow_id", req.getSignFlowId());
        }
        if (null != req.getFlowStatus()) {
            queryWrapper.eq("flow_status", req.getFlowStatus());
        }
        if (StringUtils.isNotEmpty(req.getBeginInitiateTime())) {
            queryWrapper.ge("initiator_time", req.getBeginInitiateTime());
        }
        if (StringUtils.isNotEmpty(req.getEndInitiateTime())) {
            //<=
            queryWrapper.le("initiator_time", req.getEndInitiateTime());
        }
        queryWrapper.and(i -> i.isNull("batch_status").or().eq("batch_status", Cons.BatchStr.BATCH_STATUS_SUCCESS));
        queryWrapper.orderByDesc("initiator_time");


        //queryWrapper.ne("batch_status", Cons.BatchStr.BATCH_STATUS_FAIL);
        return queryWrapper;
    }

    public QueryWrapper<YbFlowInfo> pkQueryWrapperByBatch(String institutionNumber, GetRecordInfoBatchReq req) {
        QueryWrapper<YbFlowInfo> queryWrapper = new QueryWrapper<>();
        //通过机构编码查询符合条件的流程ID
        List<YbFlowInfo> list = getSignedRecordByAreaCode(institutionNumber);
        if (!list.isEmpty()) {
            List<String> signFlowIdList = new ArrayList<>();
            list.forEach(y -> {
                signFlowIdList.add(y.getSignFlowId());
            });
            log.info("list = {}",signFlowIdList.toString());
            if (!signFlowIdList.isEmpty()) {
                queryWrapper.in("sign_flow_id", signFlowIdList);
            }
        }
        //
        if (StringUtils.isNotBlank(req.getSubject())) {
            queryWrapper.likeRight("subject", req.getSubject());
        }

        if (StringUtils.isNotEmpty(req.getBeginInitiateTime())) {
            queryWrapper.ge("initiator_time", req.getBeginInitiateTime());
        }
        if (StringUtils.isNotEmpty(req.getEndInitiateTime())) {
            //<=
            queryWrapper.le("initiator_time", req.getEndInitiateTime());
        }
        BatchQueryTypeEnum type = BatchQueryTypeEnum.getType(req.getQueryType());
        if (!req.getNumbers().isEmpty()) {
            switch (type) {
                case SIGNLE_NUMBER:
                    if (!req.getNumbers().isEmpty()) {
                        queryWrapper.like("number", req.getNumbers().get(0));
                    }
                    break;
                case SIGNLE_NAME:
                    //把机构名变为
                    if (!req.getNumbers().isEmpty()) {
                        String name = req.getNumbers().get(0);
                        //通过模糊查询，查询所有符合条件的机构number
                        List<YbInstitutionInfo> list2 = iYbInstitutionInfoService.getInstitutionInfoByName(name);
                        Set<String> tempNumber = new HashSet<>();
                        list2.forEach(y -> {
                            tempNumber.add(y.getNumber());
                        });
                        if (!tempNumber.isEmpty()) {
                            queryWrapper.in("number", tempNumber);
                        }
                    }
                    break;
                case NUMBERS:
                    if (!req.getNumbers().isEmpty()) {
                        queryWrapper.in("number", req.getNumbers());
                    }
                    break;
                case NAMES://getInstitutionInfoByName
                    //把机构名变为
                    if (!req.getNumbers().isEmpty()) {
                        //通过模糊查询，查询所有符合条件的机构number
                        List<YbInstitutionInfo> list2 = iYbInstitutionInfoService.getInstitutionInfoByName(req.getNumbers());
                        Set<String> tempNumber = new HashSet<>();
                        list2.forEach(y -> {
                            tempNumber.add(y.getNumber());
                        });
                        if (!tempNumber.isEmpty()) {
                            queryWrapper.in("number", tempNumber);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        //只查成功的
        queryWrapper.eq("flow_status", "2");
        queryWrapper.and(i -> i.isNull("batch_status").or().eq("batch_status", Cons.BatchStr.BATCH_STATUS_SUCCESS));
        queryWrapper.select("sign_flow_id");
        //        queryWrapper.orderByDesc("initiator_time");
//        queryWrapper.ne("batch_status", Cons.BatchStr.BATCH_STATUS_FAIL);
        return queryWrapper;
    }
}
