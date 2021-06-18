package com.hfi.insurance.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.benmanes.caffeine.cache.Cache;
import com.hfi.insurance.enums.ExcelVersion;
import com.hfi.insurance.model.ExcelSheetPO;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.service.InstitutionInfoService;
import com.hfi.insurance.utils.ImportExcelUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
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
public class InstitutionInfoServiceImpl implements InstitutionInfoService {

    @Resource
    private Cache<String, String> caffeineCache;

    @Value("file.path")
    private String filePath;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<InstitutionInfo> parseExcel() throws IOException {
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        // 根据后缀名称判断excel的版本
        String extName = file.getName().substring(file.getName().lastIndexOf("."));
        List<ExcelSheetPO> excelSheetList = new ArrayList<>();
        if (ExcelVersion.V2003.getSuffix().equals(extName)) {
            excelSheetList = ImportExcelUtil.readExcel(inputStream, ExcelVersion.V2003);
        } else if (ExcelVersion.V2007.getSuffix().equals(extName)) {
            excelSheetList = ImportExcelUtil.readExcel(inputStream, ExcelVersion.V2007);
        } else {
            // 无效后缀名称，这里之能保证excel的后缀名称，不能保证文件类型正确，不过没关系，在创建Workbook的时候会校验文件格式
            throw new IllegalArgumentException("Invalid excel version");
        }
        List<InstitutionInfo> list = new ArrayList<>();
        excelSheetList.forEach(excelSheetPO -> {
            List<List<Object>> dataList = excelSheetPO.getDataList();
            dataList.forEach(valueList -> {
                InstitutionInfo institutionInfo = new InstitutionInfo();
                for (int i = 1; i < valueList.size(); i++) {
                    String value = String.valueOf(valueList.get(i));
                    switch (i) {
                        case 2:
                            institutionInfo.setNumber(value);
                            break;
                        case 3:
                            institutionInfo.setClinicName(value);
                            break;
                        case 4:
                            institutionInfo.setOrgInstitutionCode(value);
                            break;
                        case 5:
                            institutionInfo.setLegalRepresentName(value);
                            break;
                        case 6:
                            institutionInfo.setContactName(value);
                            break;
                        case 7:
                            institutionInfo.setContactPhone(value);
                            break;
                        default:
                            break;
                    }
                }
                list.add(institutionInfo);
            });
        });
        String data = objectMapper.writeValueAsString(list);
        caffeineCache.put("data", data);
        return list;
    }

    @Override
    public InstitutionInfo getInstitutionInfoByNumber(String number) {
        InstitutionInfo institutionInfo = new InstitutionInfo();
        String data = caffeineCache.asMap().get("data");
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, InstitutionInfo.class);
        try {
            List<InstitutionInfo> list = objectMapper.readValue(data, listType);
            Optional<InstitutionInfo> any = list.stream().filter(clinic -> clinic.getNumber().equals(number)).findAny();
            if (any.isPresent()) {
                institutionInfo = any.get();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return institutionInfo;
    }
}
