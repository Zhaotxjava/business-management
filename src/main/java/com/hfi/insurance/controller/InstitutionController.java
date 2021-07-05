package com.hfi.insurance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.ExcelSheetPO;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.dto.InstitutionInfoQueryReq;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.utils.ImportExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/5 14:54
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping(value = "/institution")
@Api(tags = {"【机构信息管理接口】"})
public class InstitutionController {

    @Resource
    private IYbInstitutionInfoService institutionInfoService;

    @PostMapping("getInstitutionInfoList")
    @ApiOperation("分页查询机构信息")
    public ApiResponse getInstitutionInfoList(@RequestBody InstitutionInfoQueryReq req) {
        Page<YbInstitutionInfo> page =
                institutionInfoService.getInstitutionInfoList(req.getNumber(), req.getInstitutionName(), req.getPageNum(), req.getPageSize());
        return new ApiResponse(page.getRecords());
    }

    @PostMapping("import")
    public void importData(@RequestParam("file") MultipartFile multipartFile){
        List<ExcelSheetPO> excelSheetList = new ArrayList<>();
        try {
            excelSheetList = ImportExcelUtil.readExcel(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<YbInstitutionInfo> list = new ArrayList<>();
        excelSheetList.forEach(excelSheetPO -> {
            List<List<Object>> dataList = excelSheetPO.getDataList();
            dataList.forEach(valueList -> {
                YbInstitutionInfo institutionInfo = new YbInstitutionInfo();
                for (int i = 1; i < valueList.size(); i++) {
                    String value = "";
                    if (valueList.get(i) != null) {
                        value = String.valueOf(valueList.get(i)).trim();
                    }
                    switch (i) {
                        case 2:
                            institutionInfo.setNumber(value);
                            break;
                        case 3:
                            institutionInfo.setInstitutionName(value);
                            break;
                        case 4:
                            institutionInfo.setOrgInstitutionCode(value);
                            break;
                        case 5:
                            institutionInfo.setLegalName(value);
                            break;
                        case 6:
                            institutionInfo.setContactName(value);
                            break;
                        case 7:
                            institutionInfo.setContactPhone(value);
                            break;
                        case 9:
                            institutionInfo.setLegalIdCard(value);
                            break;
                        case 10:
                            institutionInfo.setLegalPhone(value);
                            break;
                        case 11:
                            institutionInfo.setContactIdCard(value);
                            break;
                        default:
                            break;
                    }
                }
                list.add(institutionInfo);
            });
        });
        institutionInfoService.saveBatch(list);
    }
}
