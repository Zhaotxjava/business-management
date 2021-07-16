package com.hfi.insurance.model.sign.req;

import com.hfi.insurance.model.InstitutionInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/14 10:43
 * @Description:
 */
@Data
public class SingerInfo {

    @ApiModelProperty("机构信息")
    private List<InstitutionInfo> institutionInfoList;

    @ApiModelProperty("用户类型;1:内部,2外部")
    private Integer accountType;

    @ApiModelProperty("关键词")
    private String key;

    @ApiModelProperty("签署方式  2-手动自由签署 3-手动坐标签署 4-手动关键字签署")
    private Integer signType;

    @ApiModelProperty("甲方签署方式 0-静默坐标签署 1-静默关键字签署 2-手动自由签署 3-手动坐标签署 4-手动关键字签署")
    private Integer partyASignType;

    @ApiModelProperty("机构签署区域 甲方,丙方,乙方...")
    private String flowName;
}
