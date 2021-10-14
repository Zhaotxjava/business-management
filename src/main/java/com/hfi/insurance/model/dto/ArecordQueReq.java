package com.hfi.insurance.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ArecordQueReq extends PageReq{
    @JsonProperty("flowId")
    private  Integer flowId;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("batchNo")
    private String batchNo;
    @JsonProperty("signFlowId")
    private String signFlowId;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date mindateTime;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date maxdateTime;

}
