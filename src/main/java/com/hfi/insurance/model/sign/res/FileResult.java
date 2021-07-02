package com.hfi.insurance.model.sign.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/2 15:46
 * @Description:
 */
@Data
@ApiModel("文件信息")
public class FileResult {
    @ApiModelProperty("文件docId")
    private Integer docId;
    @ApiModelProperty("文件fileKey")
    private String fileKey;
}
