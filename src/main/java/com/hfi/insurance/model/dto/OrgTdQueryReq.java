package com.hfi.insurance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/6 11:29
 * @Description:
 */
@Data
public class OrgTdQueryReq extends PageReq{
    @ApiModelProperty("机构编号 AKB020")
    private String number;

    @ApiModelProperty("机构名称 AKB021")
    private String institutionName;

    @ApiModelProperty(value = "机构类别 AKB022",notes = "药店-2、医院-1")
    private List<String> institutionTypes;

    @ApiModelProperty(value = "机构等级 AKA101",notes = "00社区医院,一级医院10,二级乙等20,二级甲等21,三级乙等30,三级甲等31,三级特等32,二级23")
    private List<String> institutionLevels;

    @ApiModelProperty(value = "所属区域 AAA027",
            notes = "主城区330100,萧山区330109,余杭区330110,富阳区330183,临安区330185,临平区330113,淳安县330127,建德市330182,桐庐县330122")
    private List<String> areas;

    @ApiModelProperty(value = "所属区域 AAA027",
            notes = "主城区330100,萧山区330109,余杭区330110,富阳区330183,临安区330185,临平区330113,淳安县330127,建德市330182,桐庐县330122")
    private String areaCode;

    @ApiModelProperty(value = "营利类型  BKA938",notes = "非营利1,营利2,其他9")
    private List<String> profits;

    @ApiModelProperty(value = "保险机构  BKA938",notes = "保险机构  bx")
    private String   tb;
}
