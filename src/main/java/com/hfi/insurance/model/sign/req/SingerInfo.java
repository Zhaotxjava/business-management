package com.hfi.insurance.model.sign.req;

import com.hfi.insurance.model.sign.InstitutionBaseInfo;
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
    private List<InstitutionBaseInfo> institutionInfoList;

    @ApiModelProperty("用户类型;1:内部,2外部")
    private Integer accountType;

    @ApiModelProperty("关键词")
    private String key;

    @ApiModelProperty("签署方式  2-手动自由签署 3-手动坐标签署 4-手动关键字签署")
    private Integer signType;

    @ApiModelProperty("机构签署区域 甲方,丙方,乙方...")
    private String flowName;

    public List<InstitutionBaseInfo> getInstitutionInfoList() {
        return institutionInfoList;
    }

//    public void setInstitutionInfoList(List<InstitutionBaseInfo> institutionInfoList) {
//        this.institutionInfoList = institutionInfoList;
//    }
//
//    public Integer getAccountType() {
//        return accountType;
//    }
//
//    public void setAccountType(Integer accountType) {
//        this.accountType = accountType;
//    }
//
//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }
//
//    public Integer getSignType() {
//        return signType;
//    }
//
//    public void setSignType(Integer signType) {
//        this.signType = signType;
//    }
//
//    public String getFlowName() {
//        return flowName;
//    }
//
//    public void setFlowName(String flowName) {
//        this.flowName = flowName;
//    }
}
