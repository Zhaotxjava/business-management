package com.hfi.insurance.mapper;

import com.hfi.insurance.model.YbFlowInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfi.insurance.model.dto.ArecordQueReq;

import java.util.List;

/**
 * <p>
 * 签署流程记录 Mapper 接口
 * </p>
 *
 * @author NZH
 * @since 2021-09-29
 */
public interface YbFlowInfoMapper extends BaseMapper<YbFlowInfo> {

    List<YbFlowInfo> selectYbFlowInfoList(ArecordQueReq arecordQueReq);


    List<YbFlowInfo> selectExportYbFlowInfoList(ArecordQueReq arecordQueReq);

    Integer selecttYbFlowInfoCount(ArecordQueReq arecordQueReq);
}
