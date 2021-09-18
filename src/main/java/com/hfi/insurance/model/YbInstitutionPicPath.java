package com.hfi.insurance.model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author NZH
 * @since 2021-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YbInstitutionPicPath implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 机构编码
     */
    @TableId
    private String number;

    /**
     * 分两种，yyzz,xkz
     */
    private String picType;

    /**
     * 图片地址
     */
    private String picPath;

    private Date createTime;

    private Date updateTime;


}
