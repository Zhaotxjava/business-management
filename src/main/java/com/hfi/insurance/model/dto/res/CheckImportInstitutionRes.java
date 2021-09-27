package com.hfi.insurance.model.dto.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jthealth-NZH
 * @Date 2021/9/26 17:37
 * @Describe
 * @Version 1.0
 */
@Data
public class CheckImportInstitutionRes implements Serializable {
    @ApiModelProperty("符合条件的机构编码")
    private Set<String> successSet;
    @ApiModelProperty("不符合条件的机构编码")
    private Set<String> failSet;

    public CheckImportInstitutionRes() {
        this.successSet = new HashSet<>();
        this.failSet = new HashSet<>();
    }

    public CheckImportInstitutionRes(Set<String> successSet, Set<String> failSet) {
        this.successSet = successSet;
        this.failSet = failSet;
    }
}
