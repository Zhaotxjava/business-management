package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/13 10:43
 * @Description:
 */
public class CreateSignFlowReq {

    @ApiModelProperty(value = "模板id 模板填充必传")
    private String templateId;

    @ApiModelProperty(value = "文档类型 1-文件直传 2-填充模板")
    private Integer templateType;

    @ApiModelProperty("文件fileKey 文件直传必传")
    private String fileKey;

    @ApiModelProperty("文档页数")
    private Integer pageNumber;

    @ApiModelProperty("签署信息")
    private List<SingerInfo> singerInfos;

    @ApiModelProperty("甲方签署方式 0-静默坐标签署 1-静默关键字签署 2-手动自由签署 3-手动坐标签署 4-手动关键字签署")
    private Integer partyASignType;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<SingerInfo> getSingerInfos() {
        return singerInfos;
    }

    public void setSingerInfos(List<SingerInfo> singerInfos) {
        this.singerInfos = singerInfos;
    }

    public Integer getPartyASignType() {
        return partyASignType;
    }

    public void setPartyASignType(Integer partyASignType) {
        this.partyASignType = partyASignType;
    }
}
