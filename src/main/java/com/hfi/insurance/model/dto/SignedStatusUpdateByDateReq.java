package com.hfi.insurance.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jthealth-NZH
 * @Date 2021/10/27 15:31
 * @Describe
 * @Version 1.0
 */
@Data
public class SignedStatusUpdateByDateReq implements Serializable {
    private Date start;
    private Date end;
    private String signFlowId;
}
