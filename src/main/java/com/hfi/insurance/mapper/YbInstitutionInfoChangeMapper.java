package com.hfi.insurance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.hfi.insurance.model.YbInstitutionInfoChange;
import com.hfi.insurance.model.dto.YbInstitutionInfoChangeReq;

import java.util.Date;
import java.util.List;
/**
 * @author ZTX
 * @since 2021-09-17
 */
public interface YbInstitutionInfoChangeMapper  extends BaseMapper<YbInstitutionInfoChange> {


    List<YbInstitutionInfoChange> selectChangeList(YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq);

    Integer selectChangeCount(YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq);

    List<YbInstitutionInfoChange> selectexportChangeList(YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq);
}
