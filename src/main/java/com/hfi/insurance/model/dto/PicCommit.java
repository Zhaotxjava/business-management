package com.hfi.insurance.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jthealth-NZH
 * @Date 2021/9/14 15:43
 * @Describe
 * @Version 1.0
 */
@Data
public class PicCommit {
    private List<MultipartFile> xkz;
    private List<MultipartFile> yyzz;
    private long createTime;

    public PicCommit() {
        this.xkz = new ArrayList<>();
        this.yyzz = new ArrayList<>();
        this.createTime = System.currentTimeMillis();;
    }

    public PicCommit(long createTime) {
        this.xkz = new ArrayList<>();
        this.yyzz = new ArrayList<>();
        this.createTime = createTime;
    }

    public PicCommit(List<MultipartFile> xkz, List<MultipartFile> yyzz, long createTime) {
        this.xkz = xkz;
        this.yyzz = yyzz;
        this.createTime = createTime;
    }
}
