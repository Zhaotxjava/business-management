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

    public boolean isSuccess(){
        if(ErrorCodeEnum.SUCCESS.getCode().equals(this.code) ){
            return true;
        }
        return false;
    }

    public ApiResponse(T data) {
        this.code = ErrorCodeEnum.SUCCESS.getCode();
        this.message = ErrorCodeEnum.SUCCESS.getMessage();
        this.data = data;
    }

    public ApiResponse() {

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
    public static ApiResponse success(){
        ApiResponse response = new ApiResponse();
        response.setCode(ErrorCodeEnum.SUCCESS.getCode());
        response.setMessage(ErrorCodeEnum.SUCCESS.getMessage());
        return response;
    }

    public static ApiResponse success(Object data){
        ApiResponse response = new ApiResponse().success();
        response.setData(data);
        return response;
    }

    public static ApiResponse fail(String code,String message){
        ApiResponse response = new ApiResponse();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static ApiResponse fail(String code,String message,Object data){
        ApiResponse response = new ApiResponse();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static ApiResponse fail(ErrorCodeEnum e){
        ApiResponse response = new ApiResponse();
        response.setCode(e.getCode());
        response.setMessage(e.getMessage());
        return response;
    }

    public static ApiResponse fail(ErrorCodeEnum e,String msg){
        ApiResponse response = new ApiResponse();
        response.setCode(e.getCode());
        response.setMessage(e.getMessage() +" "+ msg);
        return response;
    }

}
