package com.hfi.insurance.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.enums.ExcelVersion;
import com.hfi.insurance.model.ExcelSheetPO;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.model.dto.InstitutionInfoAddReq;
import com.hfi.insurance.service.InstitutionInfoService;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.utils.FileUploadUtil;
import com.hfi.insurance.utils.ImportExcelUtil;
import com.hfi.insurance.utils.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @Author ChenZX
 * @Date 2021/6/16 15:14
 * @Description:
 */
@Slf4j
@Service
public class InstitutionInfoServiceImpl implements InstitutionInfoService {

    @Resource
    private Cache<String, String> caffeineCache;

    @Autowired
    private OrganizationsService organizationsService;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.down.url}")
    private String fileUrl;

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
        log.info("读取excel数据总行数={}", list.size());
        String data = null;
        try {
            data = MapperUtils.obj2json(list);
        } catch (Exception e) {
            log.error("集合转json失败");
        }
        caffeineCache.put("data", data);
        log.info("缓存excel数据成功");
        return list;
    }

    @Override
    public ApiResponse getInstitutionInfoByNumber(String number) {
        InstitutionInfo institutionInfo = new InstitutionInfo();
        String data = caffeineCache.asMap().get("data");
        if (StringUtils.isEmpty(data)) {
            log.error("缓存未查询到数据");
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "数据未就绪，请稍后再试……");
        }
        List<InstitutionInfo> resultList = new ArrayList<>();
        try {
            List<InstitutionInfo> list = MapperUtils.json2list(data, InstitutionInfo.class);
            resultList = list.stream().filter(clinic -> clinic.getNumber().startsWith(number)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("json解析失败，", e);
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "系统异常，请联系管理员");
        }
        return new ApiResponse(resultList);
    }

    @Override
    public ApiResponse getInstitutionList() {
        List<InstitutionInfo> list = new ArrayList<>();
        String data = caffeineCache.asMap().get("data");
        if (null == data) {
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "数据获取失败！");
        }
        try {
            list = MapperUtils.json2list(data, InstitutionInfo.class);
        } catch (Exception e) {
            log.error("json解析失败");
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
        }
        return new ApiResponse(list);
    }

    @Override
    public ApiResponse updateInstitutionInfo(InstitutionInfoAddReq req) {
        List<InstitutionInfo> list = new ArrayList<>();
        String data = caffeineCache.asMap().get("data");
        if (null == data) {
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "数据获取失败！");
        }

        // 2>通过天印系统查询联系人是否已存在于系统，不存在则调用创建用户接口，得到用户的唯一编码，存在则直接跳到第4步
        boolean accountExist = true;
        String accountId = "";
        JSONObject accountObj = organizationsService.queryAccounts("", req.getContactIdCard());
        if (accountObj.containsKey("errCode")) {
            if ("-1".equals(accountObj.get("errCode"))) {
                accountExist = false;
            } else {
                log.error("查询外部用户信息异常，{}", accountObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), accountObj.getString("msg"));
            }
        }
        accountId = accountObj.getString("accountId");
        if (!accountExist) { //不存在则创建用户

        }

        // 3>调用天印系统查询该机构是否已存在系统，不存在则调用创建外部机构接口，存在则调用更新外部机构信息接口
        // 4>机构创建完成以后，将数据更新到缓存并保存到excel中（保存excel可异步）
        try {
            list = MapperUtils.json2list(data, InstitutionInfo.class);
            list.forEach(institutionInfo -> {
                if (req.getNumber().equals(institutionInfo.getNumber())) {
                    institutionInfo.setLegalName(req.getLegalName());
                    institutionInfo.setLegalIdCard(req.getLegalIdCard());
                    institutionInfo.setLegalPhone(req.getLegalPhone());
                    institutionInfo.setContactName(req.getContactName());
                    institutionInfo.setContactIdCard(req.getContactIdCard());
                    institutionInfo.setContactPhone(req.getContactPhone());
                }
            });
            data = MapperUtils.obj2json(list);
            //删除缓存
            caffeineCache.invalidateAll();
            //重新添加缓存
            caffeineCache.put("data", data);
        } catch (Exception e) {
            log.error("机构信息填充失败,{}", e);
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "信息保存失败");
        }
        return new ApiResponse(list);
    }

    @Override
    public ApiResponse downloadExcel(HttpServletResponse response) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        CompletableFuture<ApiResponse> future = CompletableFuture.supplyAsync(this::createNewExcel, executorService)
//                .thenApplyAsync(f -> {
//                    FileUploadUtil.download("医保定点机构列表20210607.xlsx",fileUrl,response);
//                    return f;
//                }, executorService);
//        executorService.shutdown();
//        return future.join();
        createNewExcel();
        FileUploadUtil.download("医保定点机构列表20210607.xlsx",fileUrl,response);
        return new ApiResponse(ErrorCodeEnum.SUCCESS);
    }

    private ApiResponse createNewExcel() {
        List<InstitutionInfo> list = new ArrayList<>();
        String data = caffeineCache.asMap().get("data");
        if (null == data) {
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "数据获取失败！");
        }
        try {
            list = MapperUtils.json2list(data, InstitutionInfo.class);
            log.info("数据量：{}条", list.size());
        } catch (Exception e) {
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
        }
        String[] headers = {"编号", "名称", "组织机构代码", "法定代表人", "法人身份证", "法人手机", "联系人", "联系人身份证", "联系人手机"};
        List<List<Object>> dataList = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            List<Object> institution = new ArrayList<>();
            InstitutionInfo institutionInfo = list.get(i);
            institution.add(institutionInfo.getNumber());
            institution.add(institutionInfo.getInstitutionName());
            institution.add(institutionInfo.getOrgInstitutionCode());
            institution.add(institutionInfo.getLegalName());
            institution.add(institutionInfo.getLegalIdCard());
            institution.add(institutionInfo.getLegalPhone());
            institution.add(institutionInfo.getContactName());
            institution.add(institutionInfo.getContactIdCard());
            institution.add(institutionInfo.getContactPhone());
            dataList.add(institution);
        }
        ExcelSheetPO excelSheet = new ExcelSheetPO();
        excelSheet.setHeaders(headers);
        excelSheet.setDataList(dataList);
        List<ExcelSheetPO> excelSheetList = Collections.singletonList(excelSheet);
        try {
            ImportExcelUtil.createWorkbookAtDisk(ExcelVersion.V2007, excelSheetList, fileUrl);
        } catch (IOException e) {
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
        }
        return new ApiResponse(ErrorCodeEnum.SUCCESS);
    }
}
