package com.hfi.insurance.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfi.insurance.config.ExportExcel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
@Data
public class YbCoursePl implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("序列号{唯一}")
    @TableId(value = "order_Id")
    private   String  orderId;

    @ApiModelProperty("进程id(任务id)初步与生成id一致。")
    private   String  courseId;

    @ApiModelProperty("下载任务名称")
    private   String  courseFileName;

    @ApiModelProperty("下载任务操作时间")
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private   Date courseFileDate;

    @ApiModelProperty("模板编号")
    private   String mbNumber;

    @ApiModelProperty("所包含的协议时间")
    private   String agreeDate;

    @ApiModelProperty("机构编号")
    private   String number;

    @ApiModelProperty("机构名称")
    private   String institutionName;

    @ApiModelProperty("备注")
    private   String remarks;

    @ApiModelProperty("任务执行状态 0=任务打包中 1=任务打包成功 2=任务打包失败 ")
    private   String courseStatus;

    @ApiModelProperty("下载到的流程数量")
    private   String courseCount;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private   Date createTime;


    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @ApiModelProperty("修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private   Date updateTime;

    @ApiModelProperty("地址")
    private   String urlList;

    @ApiModelProperty("统筹区编码")
    private   String areaCode;




}
