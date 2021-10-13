package com.hfi.insurance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfi.insurance.enums.Cons;
import com.hfi.insurance.mapper.YbFlowInfoMapper;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.service.IYbFlowInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 签署流程记录 服务实现类
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-19
 */
@Service
public class YbFlowInfoServiceImpl extends ServiceImpl<YbFlowInfoMapper, YbFlowInfo> implements IYbFlowInfoService {

    @Override
    public Page<YbFlowInfo> getSignedRecord(String institutionNumber, GetRecordInfoReq req) {
        Page<YbFlowInfo> page = new Page<>(req.getPageNum(), req.getPageSize());
        QueryWrapper<YbFlowInfo> queryWrapper = pkQueryWrapper(institutionNumber, req);
        return baseMapper.selectPage(page, queryWrapper);
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
//        queryWrapper.ne("batch_status", Cons.BatchStr.BATCH_STATUS_FAIL);
        return queryWrapper;
    }
}
