package com.hfi.insurance.model.sign.req;

import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/2 16:19
 * @Description:
 */
@Data
public class SignInfoBeanV2 {
    private Boolean addSignTime;
    private String certId;
    private Integer edgePosition;
    private Integer edgeScope;
    private String key;
    private Integer keyIndex;
    private String posPage;
    private Float posX;
    private Float posY;
    private String qrcodeContent;
    private Boolean qrcodeSign;
    private String sealId;
    private String sealType;
    private List<SignTimeBean> signDateInfos;
    private String signIdentity;
    private Integer signType;
    private Float width;
}
