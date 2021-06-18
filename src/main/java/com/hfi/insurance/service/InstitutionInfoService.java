package com.hfi.insurance.service;


import com.hfi.insurance.model.InstitutionInfo;

import java.io.IOException;
import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/6/16 15:02
 * @Description:
 */
public interface InstitutionInfoService {
    List<InstitutionInfo> parseExcel() throws IOException;

    InstitutionInfo getInstitutionInfoByNumber(String number);
}
