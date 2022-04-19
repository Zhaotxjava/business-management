package com.hfi.insurance.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfi.insurance.model.YbCoursePl;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface YbCoursePlMapper extends BaseMapper<YbCoursePl> {

    void insertybCoursePl(YbCoursePl ybCoursePl);

    List<YbCoursePl> selectybCoursePlList(@Param("maxDate") Date maxDate,@Param("minDate") Date minDate);
}
