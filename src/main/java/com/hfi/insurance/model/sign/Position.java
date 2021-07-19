package com.hfi.insurance.model.sign;

import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/19 13:09
 * @Description:
 */
@Data
public class Position {
    private Integer posX;
    private Integer posY;
    private String pageNo;
    private List<TemplateSignTimeInfo> templateSignTimeInfos;
}
