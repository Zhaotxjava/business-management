package com.hfi.insurance.model;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class YbCoursePl implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("序列号{唯一}")
    @TableId(value = "order_Id")
    private   Integer  orderId;

    @ApiModelProperty("进程id(任务id)初步与生成id一致。")
    private   Integer  courseId;

    @ApiModelProperty("下载任务名称")
    private   String  courseFileName;

    @ApiModelProperty("下载任务操作时间")
    private   Date courseFileDate;

    @ApiModelProperty("模板编号")
    private   String mbnNumber;

    @ApiModelProperty("所包含的协议时间")
    private   String agreeDate;

    @ApiModelProperty("机构编号")
    private   String number;

    @ApiModelProperty("机构名称")
    private   String institutionName;

    @ApiModelProperty("备注")
    private   String remarks;

    @ApiModelProperty("任务执行状态")
    private   String courseStatus;

    @ApiModelProperty("下载到的流程数量")
    private   String courseCount;

    @ApiModelProperty("创建时间")
    private   Date createTime;

    @ApiModelProperty("修改时间")
    private   Date update_time;

}
