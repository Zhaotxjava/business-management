package com.hfi.insurance.model.sign;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/14 14:35
 * @Description:
 */
@Data
@ApiModel("签署位置信息")
public class PredefineBean {
    private Boolean addSignTime;
    private String keyWord;
//    private String pageNo;
//    private Integer posX;
//    private Integer posY;
    private List<Position> positions;
    private Integer predefineId;
    private String signatureType;

}
