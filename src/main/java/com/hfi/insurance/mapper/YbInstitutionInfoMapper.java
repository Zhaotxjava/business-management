package com.hfi.insurance.mapper;

import com.hfi.insurance.model.YbInstitutionInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfi.insurance.model.dto.InstitutionInfoQueryReq;
import com.hfi.insurance.model.dto.OrgTdQueryReq;
import com.hfi.insurance.model.dto.res.InstitutionInfoRes;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>5
 * 定点机构信息 Mapper 接口
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@Repository
public interface YbInstitutionInfoMapper extends BaseMapper<YbInstitutionInfo> {
    List<String> selectNumber();

    List<YbInstitutionInfo>  selectInstitutionInfoAndOrg(@Param("institutionNumber")String institutionNumber,
                                                         @Param("number") String number,
                                                         @Param("institutionName") String institutionName,
                                                         @Param("pageNum") int pageNum,
                                                         @Param("pageSize") int pageSize);

    int selectCountInstitutionInfoAndOrg(@Param("institutionNumber")String institutionNumber,
                                         @Param("number") String number,
                                         @Param("institutionName") String institutionName);

    List<InstitutionInfoRes>  selectOrgForCreateFlow(OrgTdQueryReq req);

    int selectCountOrgForCreateFlow(OrgTdQueryReq req);

    List<YbInstitutionInfo> getInstitutionInfobxList(InstitutionInfoQueryReq institutionInfoQueryReq);

    Integer selectInstitutionInfobxCount(InstitutionInfoQueryReq institutionInfoQueryReq);

    Integer selectInstitutionInfoResCount(OrgTdQueryReq req);
}
