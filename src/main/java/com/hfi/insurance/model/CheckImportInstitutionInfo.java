package com.hfi.insurance.model;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jthealth-NZH
 * @Date 2021/10/18 11:00
 * @Describe
 * @Version 1.0
 */
@Data
public class CheckImportInstitutionInfo {
    /**
     * 机构编号
     */
    @ApiModelProperty("机构编号")
    @TableId(value = "number")
    private String number;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String institutionName;

    /**
     * 天印系统经办人用户标识
     */
    @ApiModelProperty("天印系统经办人用户标识")
    private String accountId;

    /**
     * 天印系统机构标记
     */
    @ApiModelProperty("天印系统机构标记")
    private String organizeId;
}
