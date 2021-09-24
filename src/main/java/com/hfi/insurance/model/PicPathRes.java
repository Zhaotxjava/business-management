package com.hfi.insurance.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jthealth-NZH
 * @Date 2021/9/13 9:45
 * @Describe
 * @Version 1.0
 */
@Data
@Component
public class PicPathRes {
    private String url;
    private List<String> xkzList;
    private List<String> yyzzList;
}
