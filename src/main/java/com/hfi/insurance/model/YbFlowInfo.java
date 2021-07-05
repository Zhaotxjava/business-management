package com.hfi.insurance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 签署流程记录
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YbFlowInfo implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "flow_id", type = IdType.AUTO)
    private Integer flowId;

    /**
     * 天印系统的签署流程id
     */
    private String signFlowId;

    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;


}
