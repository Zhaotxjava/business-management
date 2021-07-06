package com.hfi.insurance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/6 11:29
 * @Description:
 */
@Data
public class OrgTdQueryReq extends PageReq{
    @ApiModelProperty("机构编号")
    private String AKB020;

    @ApiModelProperty("机构名称")
    private String AKB021;

    @ApiModelProperty(value = "机构类别",notes = "药店-2、医院-1")
    private String AKB022;

    @ApiModelProperty("机构等级")
    private String AKA101;

    @ApiModelProperty(value = "所属区域",
            notes = "主城区330100,萧山区330109,余杭区330110,富阳区330183,临安区330185,临平区330113,淳安县330127,建德市330182,桐庐县330122")
    private String AAA027;

    @ApiModelProperty("营利类型 非营利1,营利2,其他9")
    private String BKA938;

}
