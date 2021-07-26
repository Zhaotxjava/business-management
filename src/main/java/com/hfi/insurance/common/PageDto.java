package com.hfi.insurance.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/23 19:36
 * @Description:
 */
@Data
public class PageDto<T> {
    @ApiModelProperty(value = "总记录数")
    private Integer total;

    @ApiModelProperty(value = "总页数")
    private Integer totalPages;

    @ApiModelProperty(value = "数据List")
    private List<T> list;
}
