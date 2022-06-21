package com.hfi.insurance.model;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
@Data
public class Management  implements Serializable {

    /**
     * 定点机构编号
     */
    @TableId(value = "AKB020")
    private String number;

    /**
     * 定点机构名称
     */
    private String institutionName;

    /**
     * 定点机构类型
     */
    private String ybInstitutionType;

    /**
     * 定点机构服务状态
     */
    private String ybInstitutionState;

    /**
     * 所属统筹区编码
     */
    private String ybInstitutionCoding;

}
