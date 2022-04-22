package com.hfi.insurance.model.sign.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author jthealth-NZH
 * @Date 2022/4/18 14:22
 * @Describe
 * @Version 1.0
 */
@Data
public class GetSignedRecordBatchRes {
    @ApiModelProperty("流程id集合")
    private Set<String> signFlowIdSet;

    @ApiModelProperty("成功查询集合")
    private Set<String> successSet;

    @ApiModelProperty("失败查询集合")
    private Set<String> failSet;

    public GetSignedRecordBatchRes(){
    }

    public GetSignedRecordBatchRes(Set<String> signFlowIdSet) {
        this.signFlowIdSet = signFlowIdSet;
    }

    public GetSignedRecordBatchRes(Set<String> signFlowIdSet, Set<String> failSet) {
        this.signFlowIdSet = signFlowIdSet;
        this.failSet = failSet;
    }

    public GetSignedRecordBatchRes(Set<String> signFlowIdSet, Set<String> successSet, Set<String> failSet) {
        this.signFlowIdSet = signFlowIdSet;
        this.successSet = successSet;
        this.failSet = failSet;
    }
}
