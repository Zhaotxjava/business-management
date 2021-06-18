package com.hengtiansoft.springbootthymeleaf.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.benmanes.caffeine.cache.Cache;
import com.hengtiansoft.springbootthymeleaf.model.ClinicInfo;
import com.hengtiansoft.springbootthymeleaf.model.ExcelSheetPO;
import com.hengtiansoft.springbootthymeleaf.service.ClinicService;
import com.hengtiansoft.springbootthymeleaf.utils.ImportExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author ChenZX
 * @Date 2021/6/16 15:14
 * @Description:
 */
@Service
public class ClinicServiceImpl implements ClinicService {

    @Resource
    private Cache<String, String> caffeineCache;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<ClinicInfo> parseExcel(MultipartFile file) {
        List<ClinicInfo> list = new ArrayList<>();
        try {
            List<ExcelSheetPO> excelSheetList = ImportExcelUtil.readExcel(file);
            excelSheetList.forEach(excelSheetPO -> {
                List<List<Object>> dataList = excelSheetPO.getDataList();
                dataList.forEach(valueList -> {
                    ClinicInfo clinicInfo = new ClinicInfo();
                    for (int i = 1; i < valueList.size(); i++) {
                        String value = String.valueOf(valueList.get(i));
                        switch (i) {
                            case 0:
                                clinicInfo.setNumber(value);
                                break;
                            case 1:
                                clinicInfo.setClinicName(value);
                                break;
                            case 2:
                                clinicInfo.setOrgInstitutionCode(value);
                                break;
                            case 3:
                                clinicInfo.setLegalRepresentName(value);
                                break;
                            case 4:
                                clinicInfo.setContactName(value);
                                break;
                            case 5:
                                clinicInfo.setContactPhone(value);
                                break;
                            default:
                                break;
                        }
                    }
                    list.add(clinicInfo);
                });
            });
            String data = objectMapper.writeValueAsString(list);
            caffeineCache.put("data", data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ClinicInfo getClinicInfoByNumber(String number) {
        ClinicInfo clinicInfo = new ClinicInfo();
        String data = caffeineCache.asMap().get("data");
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, ClinicInfo.class);
        try {
            List<ClinicInfo> list = objectMapper.readValue(data, listType);
            Optional<ClinicInfo> any = list.stream().filter(clinic -> clinic.getNumber().equals(number)).findAny();
            if (any.isPresent()){
                clinicInfo = any.get();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return clinicInfo;
    }
}
