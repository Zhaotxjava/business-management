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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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

    private BlockingQueue<InstitutionInfo> writeFileQueue = new LinkedBlockingQueue<>();

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
        // 1>从缓存中查询本条记录
        InstitutionInfo cacheInfo = new InstitutionInfo();
        try {
            list = MapperUtils.json2list(data, InstitutionInfo.class);
            Optional<InstitutionInfo> any = list.stream().filter(clinic -> clinic.getNumber().equals(req.getNumber())).findAny();
            if (any.isPresent()) {
                cacheInfo = any.get();
            }
        } catch (Exception e) {
            log.error("缓存中读取机构信息失败,{}", e);
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "信息保存失败");
        }
        // 2>通过天印系统查询联系人是否已存在于系统，不存在则调用创建用户接口，得到用户的唯一编码，存在则直接跳到第4步
        boolean accountExist = true;
        boolean organExist = true;
        String accountId = "";
        String organizeId = "";
        JSONObject accountObj = organizationsService.queryAccounts("", req.getContactIdCard());
        if (accountObj.containsKey("errCode")) {
            if ("-1".equals(accountObj.getString("errCode"))) {
                accountExist = false;
            } else {
                log.error("查询外部用户信息异常，{}", accountObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), accountObj.getString("msg"));
            }
        }
        if (!accountExist) { //不存在则创建用户
            JSONObject resultObj = organizationsService.createAccounts(req.getContactName(), req.getContactIdCard(), req.getContactPhone());
            if (resultObj.containsKey("errCode")) {
                log.error("创建外部用户信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            accountId = resultObj.getString(accountId);
        } else {
            //系统已存在则更新用户信息
            accountId = accountObj.getString("accountId");
            JSONObject resultObj = organizationsService.updateAccounts(accountId, req.getContactName(), req.getContactIdCard(), req.getContactPhone());
            if (resultObj.containsKey("errCode")) {
                log.error("更新外部用户信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
        }
        // 3>调用天印系统查询该机构是否已存在系统，不存在则调用创建外部机构接口，存在则调用更新外部机构信息接口
        JSONObject organObj = organizationsService.queryOrgans("", req.getNumber());
        if (organObj.containsKey("errCode")) {
            if ("-1".equals(organObj.getString("errCode"))) {
                organExist = false;
            } else {
                log.error("查询外部机构信息异常，{}", organObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), accountObj.getString("msg"));
            }
        }
        InstitutionInfo institutionInfo = new InstitutionInfo();
        BeanUtils.copyProperties(req, institutionInfo);
        institutionInfo.setInstitutionName(cacheInfo.getInstitutionName());
        if (!organExist) { //不存在则创建机构
            //institutionInfo.setAccountId(accountId); //创建默认经办人
            JSONObject resultObj = organizationsService.createOrgans(institutionInfo);
            if (resultObj.containsKey("errCode")) {
                log.error("创建外部机构信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            organizeId = accountObj.getString("organizeId");
        } else {
            //更新机构信息
            organizeId = accountObj.getString("organizeId");
            institutionInfo.setOrganizeId(organizeId);
            JSONObject resultObj = organizationsService.updateOrgans(institutionInfo);
            if (resultObj.containsKey("errCode")) {
                log.error("更新外部用户信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            //判定是否要重新绑定用户
            if (StringUtils.isNotEmpty(cacheInfo.getAccountId())) {
                resultObj = organizationsService.unbindAgent(cacheInfo.getAccountId(), institutionInfo.getNumber(), accountId, institutionInfo.getContactIdCard());
                if (resultObj.containsKey("errCode")) {
                    log.error("外部机构解绑经办人信息异常，{}", resultObj);
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
                }
            }
        }
        JSONObject resultObj = organizationsService.bindAgent(organizeId, institutionInfo.getNumber(), accountId, institutionInfo.getContactIdCard());
        if (resultObj.containsKey("errCode")) {
            log.error("外部机构绑定经办人信息异常，{}", resultObj);
            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        institutionInfo.setAccountId(accountId);
        institutionInfo.setOrganizeId(organizeId);
        institutionInfo.setUpdateTime(df.format(new Date()));
        // 4>机构创建完成以后，将数据更新到缓存并保存到excel中（保存excel可异步）
        try {
            list.forEach(tmp -> {
                if (req.getNumber().equals(tmp.getNumber())) {
                    tmp.setOrgInstitutionCode(req.getOrgInstitutionCode());
                    tmp.setLegalName(req.getLegalName());
                    tmp.setLegalIdCard(req.getLegalIdCard());
                    tmp.setLegalPhone(req.getLegalPhone());
                    tmp.setContactName(req.getContactName());
                    tmp.setContactIdCard(req.getContactIdCard());
                    tmp.setContactPhone(req.getContactPhone());
                    tmp.setAccountId(institutionInfo.getAccountId());
                    tmp.setOrganizeId(institutionInfo.getOrganizeId());
                    tmp.setUpdateTime(institutionInfo.getUpdateTime());
                }
            });
            data = MapperUtils.obj2json(list);
            //删除缓存
            caffeineCache.invalidateAll();
            //重新添加缓存
            caffeineCache.put("data", data);
        } catch (Exception e) {
            log.error("更新缓存失败,{}", e);
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "信息保存失败");
        }
        // todo 另起线程处理excel
        try {
            writeFileQueue.put(institutionInfo);
        } catch (Exception e) {
            log.error("添加队列异常", e);
        }
        return new ApiResponse(list);
    }

    @Override
    public void downloadExcel(HttpServletResponse response) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        CompletableFuture<ApiResponse> future = CompletableFuture.supplyAsync(this::createNewExcel, executorService)
//                .thenApplyAsync(f -> {
//                    FileUploadUtil.download("医保定点机构列表20210607.xlsx",fileUrl,response);
//                    return f;
//                }, executorService);
//        executorService.shutdown();
//        return future.join();
        try {
            createNewExcel();
        } catch (Exception e) {
            log.error("生成新的excel失败，原因：{}",e.getMessage());
        }
        FileUploadUtil.download("医保定点机构列表20210607.xlsx", fileUrl, response);
    }

    private void createNewExcel() throws Exception {
        List<InstitutionInfo> list = new ArrayList<>();
        String data = caffeineCache.asMap().get("data");
        if (null == data) {
            return;
        }
        list = MapperUtils.json2list(data, InstitutionInfo.class);
        log.info("数据量：{}条", list.size());
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
        ImportExcelUtil.createWorkbookAtDisk(ExcelVersion.V2007, excelSheetList, fileUrl);
    }

    @PostConstruct
    private void consumer() {
        log.info("启动-开启写文件线程");
        WriteFileThread writeFileThread = new WriteFileThread(writeFileQueue);
        writeFileThread.start();
    }

    private class WriteFileThread extends Thread {
        private BlockingQueue<InstitutionInfo> writeFileQueue;

        public WriteFileThread(BlockingQueue<InstitutionInfo> writeFileQueue) {
            this.writeFileQueue = writeFileQueue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    InstitutionInfo institutionInfo = writeFileQueue.take();
                    log.info("开始写文件={}", institutionInfo);
                } catch (InterruptedException e) {
                    log.error("更新文件异常", e);
                }
            }
        }
    }
}
