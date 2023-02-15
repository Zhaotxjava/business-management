package com.hfi.spaf.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页参数
 * */
@Data
public class PageDto {

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数")
    private long totalCount;
    /**
     * 每页记录数
     */
    @ApiModelProperty(value = "每页记录数")
    private Integer pageSize = 20;
    /**
     * 总页数
     */
    @ApiModelProperty(value = "总页数")
    private Integer totalPage;
    /**
     * 当前页数
     */
    @ApiModelProperty(value = "当前页数")
    private Integer pageNum = 1;

}
