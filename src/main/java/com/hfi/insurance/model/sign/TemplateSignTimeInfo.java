package com.hfi.insurance.model.sign;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/14 16:54
 * @Description:
 */
@ApiModel("签署时间信息")
@Data
public class TemplateSignTimeInfo {
    private String id;
    private Integer fontSize;
    private String dateFormat;
    private String posPage;
    private Integer posX;
    private Integer posY;
}
