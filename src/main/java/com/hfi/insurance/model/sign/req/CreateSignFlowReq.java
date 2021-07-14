package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/13 10:43
 * @Description:
 */
@Data
public class CreateSignFlowReq {


    @ApiModelProperty(value = "模板id 模板填充必传")
    private String templateId;

    @ApiModelProperty(value = "文档类型 1-文件直传 2-填充模板")
    private Integer templateType;

    @ApiModelProperty("文件fileKey 文件直传必传")
    private String fileKey;

    @ApiModelProperty("文档页数")
    private Integer pageNumber;

    @ApiModelProperty("签署信息")
    private List<SingerInfo> singerInfos;

    @ApiModelProperty("机构名称")
    private List<String> institutionNameList;


}
