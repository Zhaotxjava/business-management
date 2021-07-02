package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/2 16:27
 * @Description:
 */
@Data
@ApiModel("签章日期信息")
public class SignTimeBean {
    private String dateFormat;
    private Integer fontSize;
    private String posPage;
    private Float posX;
    private Float posY;

}
