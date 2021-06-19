package com.hfi.insurance.utils;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class FileUploadConstant {

    @Value("${file.path}")
    private  String uploadFilePath;

    @Value("${file.down.url}")
    private  String uploadFileUrl;

}
