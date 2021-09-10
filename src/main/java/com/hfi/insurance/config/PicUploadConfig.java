package com.hfi.insurance.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author jthealth-NZH
 * @Date 2021/9/8 11:31
 * @Describe
 * @Version 1.0
 */

@Configuration
@Data
public class PicUploadConfig {
    @Value("${file.picPath}")
    private String uploadPathImg;
}
