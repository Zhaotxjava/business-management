package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/16 18:17
 * @Description:
 */
@Data
public class GetPageWithPermissionReq {
    @ApiModelProperty("开始时间")
    private String createTime;

    @ApiModelProperty("用户id，用户id与机构id必填其一")
    private String accountId;

    @ApiModelProperty("机构id，用户id与机构id必填其一")
    private String departmentId;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("当前页，当前页不能为负数")
    private Integer pageNum;

    @ApiModelProperty("单页数量，单页数据量不应超过30")
    private Integer pageSize;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板编号")
    private String templateNum;

    @ApiModelProperty("模板类型Id")
    private Integer typeId;
}
