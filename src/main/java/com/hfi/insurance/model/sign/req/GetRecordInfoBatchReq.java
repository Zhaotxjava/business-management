package com.hfi.insurance.model.sign.req;

import com.hfi.insurance.config.ExportExcel;
import com.hfi.insurance.model.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Author ChenZX
 * @Date 2021/7/19 18:10
 * @Description:
 */
@Data
public class GetRecordInfoBatchReq extends PageReq {

    @ApiModelProperty("序列号")
    private String orderId;

    @ApiModelProperty("模板编号")
    private String templateId;

    @ApiModelProperty("统筹区编码")
    private String areaCode;

    @ApiModelProperty("批量状态 PLPD=需要返回图中没有的机构编码，或者机构名称! PLTG=不需要返回直接发起下载通知")
    private String plType;

    @ApiModelProperty("模板编号前缀")
    private String subject;

    @ApiModelProperty("查询方式，BLANK=numberSet为空格，SIGNLE_NUMBER=单个机构编码，SIGNLE_NAME=单个机构名称，NUMBERS=多个机构编码，NAMES=多个机构名称")
    private String queryType;

    @ApiModelProperty("查询入参,包含机构编码、机构名称")
    private List<String> numbers;

    @ApiModelProperty("发起日期")
    private String beginInitiateTime;

    @ApiModelProperty("结束日期")
    private String endInitiateTime;

}
