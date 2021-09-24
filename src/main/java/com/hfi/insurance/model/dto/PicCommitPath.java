package com.hfi.insurance.model.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
@Component
public class PicCommitPath {

    private String url;
    private List<String> xkzList;
    private List<String> yyzzList;
    private long createTime;

    public PicCommitPath() {
        this.xkzList = new ArrayList<>();
        this.yyzzList = new ArrayList<>();
        this.createTime = System.currentTimeMillis();;
    }

    public PicCommitPath(String url) {
        this.xkzList = new ArrayList<>();
        this.yyzzList = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
        this.url = url;
    }

    public PicCommitPath(long createTime) {
        this.xkzList = new ArrayList<>();
        this.yyzzList = new ArrayList<>();
        this.createTime = createTime;
    }

    public PicCommitPath(List<String> xkz, List<String> yyzz, long createTime) {
        this.xkzList = xkz;
        this.yyzzList = yyzz;
        this.createTime = createTime;
    }
}
