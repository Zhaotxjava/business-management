package com.hfi.insurance.model.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
@Data
public class YbInstitutionInfoChangeReq   extends PageReq{

    @ApiModelProperty("机构编号")
    private  String   number;
    @ApiModelProperty("机构名称")
    private  String  institutionName;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date minupdateTime;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date maxupdateTime;


}
