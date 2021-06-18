package com.hfi.insurance.common;

import com.hfi.insurance.enums.ErrorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "ApiResponse",description = "统一相应包")
public class ApiResponse<T>  {

    @ApiModelProperty(value = "返回成功码")
    private String code;
    @ApiModelProperty(value = "返回消息")
    private String message;
    @ApiModelProperty(value = "返回数据")
    private T data;


    public ApiResponse(T data) {
        this.code = ErrorCodeEnum.SUCCESS.getCode();
        this.message = ErrorCodeEnum.SUCCESS.getMessage();
        this.data = data;
    }

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
