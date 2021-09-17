package com.hfi.insurance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.hfi.insurance.model.YbInstitutionInfoChange;
import com.hfi.insurance.model.dto.YbInstitutionInfoChangeReq;

import java.util.Date;
import java.util.List;

public interface YbInstitutionInfoChangeMapper  extends BaseMapper<YbInstitutionInfoChange> {


    List<YbInstitutionInfoChange> selectChangeList(YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq);

    Integer selectChangeCount(YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq);
}
