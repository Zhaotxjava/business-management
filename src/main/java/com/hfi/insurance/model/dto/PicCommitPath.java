package com.hfi.insurance.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jthealth-NZH
 * @Date 2021/9/14 15:43
 * @Describe
 * @Version 1.0
 */
@Data
public class PicCommitPath {
    private List<String> xkz;
    private List<String> yyzz;
    private long createTime;

    public PicCommitPath() {
        this.xkz = new ArrayList<>();
        this.yyzz = new ArrayList<>();
        this.createTime = System.currentTimeMillis();;
    }

    public PicCommitPath(long createTime) {
        this.xkz = new ArrayList<>();
        this.yyzz = new ArrayList<>();
        this.createTime = createTime;
    }

    public PicCommitPath(List<String> xkz, List<String> yyzz, long createTime) {
        this.xkz = xkz;
        this.yyzz = yyzz;
        this.createTime = createTime;
    }
}
