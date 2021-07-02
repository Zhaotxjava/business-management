package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/2 11:15
 * @Description:
 */
@ApiModel("获取文档模板列表请求dto")
@Data
public class GetPageWithPermissionV2Model {
    @ApiModelProperty("开始时间")
    private String createTime;

    @ApiModelProperty("用户id，用户id与机构id必填其一")
    private String accountId;

    @ApiModelProperty("机构id，用户id与机构id必填其一")
    private String departmentId;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("当前页，当前页不能为负数")
    private Integer pageIndex;

    @ApiModelProperty("单页数量，单页数据量不应超过30")
    private Integer pageSize;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板编号")
    private String templateNum;

    @ApiModelProperty("模板类型Id")
    private Integer typeId;
}
