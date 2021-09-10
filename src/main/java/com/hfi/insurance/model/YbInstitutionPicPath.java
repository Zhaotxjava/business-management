package com.hfi.insurance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author NZH
 * @since 2021-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YbInstitutionPicPath implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机构编码
     */
    private String number;

    /**
     * 分两种，yyzz,xkz
     */
    private String picType;

    /**
     * 图片地址
     */
    private String picPath;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
