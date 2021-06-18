package com.hengtiansoft.springbootthymeleaf.service;

import com.hengtiansoft.springbootthymeleaf.model.ClinicInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/6/16 15:02
 * @Description:
 */
public interface ClinicService {
    List<ClinicInfo> parseExcel(MultipartFile file);

    ClinicInfo getClinicInfoByNumber(String number);
}
