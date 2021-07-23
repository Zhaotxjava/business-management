package com.hfi.insurance.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Setter;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/23 19:36
 * @Description:
 */
@Setter
public class PageDto<T> {
    @ApiModelProperty(value = "总记录数")
    private Long total;

    @ApiModelProperty(value = "总页数")
    private Integer totalPages;

    @ApiModelProperty(value = "数据List")
    private List<T> list;
}
