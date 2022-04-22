package com.hfi.insurance.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfi.insurance.model.YbCoursePl;
import com.hfi.insurance.model.sign.req.GetRecordInfoBatchReq;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface YbCoursePlMapper extends BaseMapper<YbCoursePl> {

    void insertybCoursePl(YbCoursePl ybCoursePl);

    List<YbCoursePl> selectybCoursePlList(@Param("maxDate") Date maxDate,@Param("minDate") Date minDate);


    void delectCoursePlList(@Param("minDate") Date minDate);

    List<YbCoursePl> selectSignInfoList(@Param("areaCode") String areaCode,@Param("templateId") String templateId,  @Param("maxDate") Date maxDate, @Param("minDate")Date minDate, @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
}
