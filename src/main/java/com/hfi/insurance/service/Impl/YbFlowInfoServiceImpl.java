package com.hfi.insurance.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public Page<YbFlowInfo> getSignedRecord(GetRecordInfoReq req) {
        QueryWrapper<YbFlowInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(req.getSubject())) {
            queryWrapper.like("subject", req.getSubject());
        }
        if (StringUtils.isNotBlank(req.getSignStatus())) {
            queryWrapper.eq("sign_status", req.getSignStatus());
        }
        if (null != req.getFlowId()) {
            queryWrapper.eq("flow_id", req.getFlowId());
        }
        if (null != req.getFlowStatus()) {
            queryWrapper.eq("flow_status", req.getFlowStatus());
        }
        if (StringUtils.isNotEmpty(req.getBeginInitiateTime())) {
            queryWrapper.ge("create_time", req.getBeginInitiateTime());
        }
        if (StringUtils.isNotEmpty(req.getEndInitiateTime())) {
            //<=
            queryWrapper.le("create_time", req.getEndInitiateTime());
        }
        Page<YbFlowInfo> page = new Page<>(req.getPageNum(), req.getPageSize());

        return baseMapper.selectPage(page, queryWrapper);
    }
}
