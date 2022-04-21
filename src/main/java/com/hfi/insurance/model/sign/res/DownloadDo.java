package com.hfi.insurance.model.sign.res;

import lombok.Data;
/**
 * @Author ZTX
 * @Date 2022/4/20
 * @Description:
 */
@Data
public class DownloadDo {

    private  String id;

    private  String bizNo;

    private  String processIds;

    private  String fileKey;

    private  String fileName;

    private  String downloadUrl;

    private  String status;

    private  String message;

    private  String fileCount;

}
