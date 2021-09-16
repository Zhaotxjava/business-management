package com.hfi.insurance.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author jthealth-NZH
 * @Date 2021/9/16 19:53
 * @Describe
 * @Version 1.0
 */
@Data
public class GetPicByNumbers {
    @JsonProperty("number")
    private List<String> number;
    private String orgInstitutionCode;
}
