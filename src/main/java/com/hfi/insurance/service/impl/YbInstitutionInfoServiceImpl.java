package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.common.ExcelUtil;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.mapper.*;
import com.hfi.insurance.model.*;
import com.hfi.insurance.model.dto.*;
import com.hfi.insurance.model.dto.res.InstitutionInfoRes;
import com.hfi.insurance.model.sign.BindedAgentBean;
import com.hfi.insurance.model.sign.QueryOuterOrgResult;
import com.hfi.insurance.model.sign.YbFlowDownload;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.utils.FTPUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 定点机构信息 服务实现类
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@Slf4j
@Service
public class YbInstitutionInfoServiceImpl extends ServiceImpl<YbInstitutionInfoMapper, YbInstitutionInfo> implements IYbInstitutionInfoService {

    @Resource
    private YbInstitutionInfoMapper institutionInfoMapper;

    @Resource
    private YbOrgTdMapper orgTdMapper;

    @Autowired
    private OrganizationsService organizationsService;

    @Resource
    private Cache<String, String> caffeineCache;
    @Resource
    private YbInstitutionInfoChangeMapper ybInstitutionInfoChangeMapper;
    @Autowired
    private YbInstitutionPicPathMapper ybInstitutionPicPathMapper;
    @Autowired
    private FTPUploadUtil ftpUploadUtil;

    @Autowired
    private YbFlowInfoMapper ybFlowInfoMapper;
    @Autowired
    private YbInstitutionInfoMapper ybInstitutionInfoMapper;


    @Override
    @LogAnnotation
    public ApiResponse getInstitutionInfoList(String token, String number, String institutionName, int current, int limit) {
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = requestAttributes.getRequest();
//        HttpSession session =  request.getSession();
//        String institutionNumber = (String) session.getAttribute("number");
        String jsonStr = caffeineCache.asMap().get(token);
        log.info("token:{}", token);
        if (StringUtils.isBlank(jsonStr)) {
            return new ApiResponse(ErrorCodeEnum.TOKEN_EXPIRED.getCode(), ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String institutionNumber = jsonObject.getString("number");
        log.info("从token中获取机构编号：{}", institutionNumber);
//        if (StringUtils.isNotBlank(institutionNumber)){
//            queryWrapper.like("number",institutionNumber);
//        }
//        if (StringUtils.isNotBlank(number)){
//            queryWrapper.eq("number",number);
//        }
//        if (StringUtils.isNotBlank(institutionName)){
//            queryWrapper.eq("institution_name",institutionName);
//        }


        List<YbInstitutionInfo> ybInstitutionInfos = institutionInfoMapper.selectInstitutionInfoAndOrg(institutionNumber, number, institutionName, (current - 1) * limit, limit);
        int total = institutionInfoMapper.selectCountInstitutionInfoAndOrg(institutionNumber, number, institutionName);
        Page<YbInstitutionInfoChange> page = new Page<>(current, limit);


        List<YbInstitutionInfoChange> list = new ArrayList<>();
        QueryWrapper<YbInstitutionPicPath> objectQueryWrapper = new QueryWrapper<>();

        for (int i = 0; i < ybInstitutionInfos.size(); i++) {
            YbInstitutionInfo ybInstitutionInfo = ybInstitutionInfos.get(i);
            YbInstitutionInfoChange change = new YbInstitutionInfoChange();
            BeanUtils.copyProperties(ybInstitutionInfo, change);
            objectQueryWrapper.eq("number", ybInstitutionInfo.getNumber());
            YbInstitutionPicPath ybInstitutionPicPath = ybInstitutionPicPathMapper.selectOne(objectQueryWrapper);
            objectQueryWrapper.clear();
            if (Objects.isNull(ybInstitutionPicPath)) {

            } else {
                log.info("ybInstitutionPicPath.getPicPath() ={}", ybInstitutionPicPath.getPicPath());
                JSONObject jsonObject1 = JSONObject.parseObject(ybInstitutionPicPath.getPicPath());
                change.setLicensePicture(String.valueOf(jsonObject1.get("xkzList")));
                change.setBusinessPicture(String.valueOf(jsonObject1.get("yyzzList")));
            }
            list.add(change);
        }

//        page.setRecords(ybInstitutionInfos);
        page.setRecords(list);
        page.setTotal(total);
        return new ApiResponse(page);
    }

    @Override
    @LogAnnotation
    public Page<InstitutionInfoRes> getOrgTdListForCreateFlow(OrgTdQueryReq req) {
        Integer pageNum = req.getPageNum();
        req.setPageNum((pageNum - 1) * req.getPageSize());
        List<InstitutionInfoRes> ybInstitutionInfos = institutionInfoMapper.selectOrgForCreateFlow(req);

        log.info("过滤前有{}个，{}", ybInstitutionInfos.size(), JSONObject.toJSONString(ybInstitutionInfos));
        ybInstitutionInfos.removeIf(item -> (StringUtils.isBlank(item.getAccountId()))
                || StringUtils.isBlank(item.getOrganizeId())
                || StringUtils.isBlank(item.getLegalIdCard())
        );
        log.info("过滤后有{}个，{}", ybInstitutionInfos.size(), JSONObject.toJSONString(ybInstitutionInfos));
        //todo 添加保险公司

        int pageIndex = 1;
        int size = 1;
        List<InstitutionInfoRes> insuranceList = new ArrayList<>();
        List<QueryOuterOrgResult> queryOuterOrgResultList = new ArrayList<>();
        while (size > 0) {
            String orgInfoListStr = organizationsService.queryByOrgName("", pageIndex);
            JSONObject object = JSONObject.parseObject(orgInfoListStr);
            log.info("getOrgTdListForCreateFlow 调用E签宝queryByOrgName查询机构{}", object.toJSONString());
            if ("0".equals(object.getString("errCode"))) {
                String data = object.getString("data");
                List<QueryOuterOrgResult> queryOuterOrgResults = JSON.parseArray(data, QueryOuterOrgResult.class);
                if (0 == queryOuterOrgResults.size()) {
                    size = 0;
                }
                pageIndex++;
                queryOuterOrgResultList.addAll(queryOuterOrgResults);
            } else {
                break;
            }
        }
        log.info("外部机构数量：【{}】", queryOuterOrgResultList.size());
        for (QueryOuterOrgResult result : queryOuterOrgResultList) {
            BindedAgentBean bindedAgentBean = CollectionUtils.firstElement(result.getAgentAccounts());
            String organizeNo = result.getOrganizeNo();
            if (bindedAgentBean != null && organizeNo.startsWith("bx")) {
                InstitutionInfoRes res = new InstitutionInfoRes();
                res.setAccountId(bindedAgentBean.getAgentId());
                res.setContactName(bindedAgentBean.getAgentName());
                res.setOrganizeId(result.getOrganizeId());
                res.setNumber(organizeNo);
                res.setInstitutionName(result.getOrganizeName());
                insuranceList.add(res);
            }
        }
        ybInstitutionInfos.addAll(insuranceList);


        int total = institutionInfoMapper.selectCountOrgForCreateFlow(req);
        Page<InstitutionInfoRes> page = new Page<>(req.getPageNum(), req.getPageSize());
        page.setRecords(ybInstitutionInfos);
        page.setTotal(ybInstitutionInfos.size());
        return page;
    }

    @Override
    @LogAnnotation
    public YbInstitutionInfo getInstitutionInfo(String number) {
        QueryWrapper<YbInstitutionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("number", number);
        return institutionInfoMapper.selectOne(queryWrapper);
    }

    @Override
    @LogAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse updateInstitutionInfo(InstitutionInfoAddReq req) {
        String number = req.getNumber();
        //1.往yb_institution_info表添加记录
        YbInstitutionInfo cacheInfo = this.getInstitutionInfo(number);
        QueryWrapper<YbOrgTd> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("AKB020", number);
        YbOrgTd orgTd = orgTdMapper.selectOne(queryWrapper);
        if (null == orgTd) {
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "机构不存在!");
        }

        if (null == cacheInfo) {
            YbInstitutionInfo institutionInfo = new YbInstitutionInfo();
            institutionInfo.setNumber(number)
                    .setInstitutionName(orgTd.getAkb021())
                    .setContactIdCard(req.getContactIdCard())
                    .setContactName(req.getContactName())
                    .setContactPhone(req.getContactPhone())
                    .setLegalIdCard(req.getLegalIdCard())
                    .setLegalName(req.getLegalName())
                    .setLegalPhone(req.getLegalPhone())
                    .setOrgInstitutionCode(req.getOrgInstitutionCode());
            institutionInfoMapper.insert(institutionInfo);
            cacheInfo = this.getInstitutionInfo(number);
        }

        // 用于本地机构存储更新的数据结构
        InstitutionInfo institutionInfo = new InstitutionInfo();
        BeanUtils.copyProperties(req, institutionInfo);
        institutionInfo.setInstitutionName(orgTd.getAkb021());

        // 是否更新
        YbInstitutionInfoChange change = new YbInstitutionInfoChange();
        BeanUtils.copyProperties(cacheInfo, change);

        // 2>通过天印系统查询联系人是否已存在于系统，不存在则调用创建用户接口，得到用户的唯一编码，存在则直接跳到第4步
        boolean accountExist = true;
        boolean organExist = true;
        boolean isSameAccount = true;  //法人和经办人是否同一个
        String accountId = "";
        String defaultAccountId = ""; //法人默认经办人
        String organizeId = "";
        //法人身份证是否等于联系人身份证
        if (!req.getLegalIdCard().equals(req.getContactIdCard())) {
            isSameAccount = false;
        }
        // 判定法人是否已存在系统用户
        log.info("机构信息：【{}】", JSON.toJSONString(cacheInfo));
        String legalAccountId = cacheInfo.getLegalAccountId() != null ? cacheInfo.getLegalAccountId() : "";
        //根据天印系统法人用户标识去查法人是否存在
        JSONObject accountObj = organizationsService.queryAccounts(legalAccountId, "");

        log.info("查询外部用户【{}】接口响应{}", legalAccountId, accountObj);
        //天印系统经办人用户标识  是空的话 false 法人人不存在
        if (null == accountObj.getString("accountId")) {
            accountExist = false;
        }
        if (accountObj.containsKey("errCode")) {
            if ("-1".equals(accountObj.getString("errCode"))) {
                accountExist = false;
            } else {
                log.error("查询外部用户（法人）信息异常，{}", accountObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), accountObj.getString("msg"));
            }
        }

        if (!accountExist) { //不存在则创建用户
            JSONObject resultObj = organizationsService.createAccounts(req.getLegalName(), req.getLegalIdCard(), req.getLegalPhone());
            if (resultObj.containsKey("errCode")) {
                log.error("创建外部用户（法人）信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            defaultAccountId = resultObj.getString("accountId");
        } else {
            //天印系统法人用户标识
            if (StringUtils.isNotBlank(legalAccountId)) {
                defaultAccountId = legalAccountId;

                JSONObject resultObj = organizationsService.updateAccounts(legalAccountId, req.getLegalName(), req.getLegalIdCard(), req.getLegalPhone());
                if (resultObj.containsKey("errCode")) {
                    log.error("更新外部用户（法人）信息异常，{}", resultObj);
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
                }
            }
        }
        // 更新机构信息（法人accountId）
        institutionInfo.setLegalAccountId(defaultAccountId);
        updateInstitutionInfo(institutionInfo);

        boolean agentAccountExist = true;
        if (!isSameAccount) {
            log.info("法人和经办人信息不一致，再创建联系人为经办人");
            String agentAccountId = cacheInfo.getAccountId() != null ? cacheInfo.getAccountId() : "";
            //判断经办人（联系人）是否存在于系统内
            JSONObject agentAccountObj = organizationsService.queryAccounts(agentAccountId, "");
            log.info("查询外部用户【{}】接口响应{}", accountId, agentAccountObj);
            if (null == agentAccountObj.getString("accountId")) {
                agentAccountExist = false;
            }
            if (agentAccountObj.containsKey("errCode")) {
                if ("-1".equals(agentAccountObj.getString("errCode"))) {
                    agentAccountExist = false;
                } else {
                    log.error("查询外部用户（联系人）信息异常，{}", agentAccountObj);
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), agentAccountObj.getString("msg"));
                }
            }
            //不存在则创建用户
            if (!agentAccountExist) {
                JSONObject resultObj = organizationsService.createAccounts(req.getContactName(), req.getContactIdCard(), req.getContactPhone());
                if (resultObj.containsKey("errCode")) {
                    log.error("创建外部用户（联系人）信息异常，{}", resultObj);
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
                }
                accountId = resultObj.getString("accountId");
            } else {
                if (StringUtils.isNotBlank(agentAccountId)) {
                    accountId = agentAccountId;
                    JSONObject resultObj = organizationsService.updateAccounts(agentAccountId, req.getContactName(), req.getContactIdCard(), req.getContactPhone());
                    if (resultObj.containsKey("errCode")) {
                        log.error("更新外部用户（联系人）信息异常，{}", resultObj);
                        return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
                    }
                }
            }
        } else {
            accountId = defaultAccountId;
        }
        // 更新机构信息（经办人accountId）
        institutionInfo.setAccountId(accountId);
        updateInstitutionInfo(institutionInfo);

        // 3>调用天印系统查询该机构是否已存在系统，不存在则调用创建外部机构接口，存在则调用更新外部机构信息接口
        JSONObject organObj = organizationsService.queryOrgans("", number);
        if (organObj.containsKey("errCode")) {
            if ("-1".equals(organObj.getString("errCode"))) {
                organExist = false;
            } else {
                log.error("查询外部机构信息异常，{}", organObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), organObj.getString("msg"));
            }
        }
        if (!organExist) { //不存在则创建机构
            JSONObject resultObj = organizationsService.createOrgans(institutionInfo);
            if (resultObj.containsKey("errCode")) {
                log.error("创建外部机构信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            organizeId = resultObj.getString("organizeId");
        } else {
            //更新机构信息
            organizeId = organObj.getString("organizeId");
            JSONObject resultObj = organizationsService.updateOrgans(institutionInfo);
            if (resultObj.containsKey("errCode")) {
                log.error("更新外部用户信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            //todo 法人信息变更，将法人信息添加为经办人
            if (!StringUtils.equals(defaultAccountId, organObj.getString("agentAccountId"))) {
                resultObj = organizationsService.bindAgent(organizeId, institutionInfo.getNumber(), defaultAccountId, institutionInfo.getLegalIdCard());
                if (resultObj.containsKey("errCode")) {
                    log.error("外部机构将法人绑定为经办人信息异常，{}", resultObj);
                }
            }
        }
        if (!isSameAccount) {
            JSONObject resultObj = null;
            if (StringUtils.isNotBlank(cacheInfo.getAccountId()) && !StringUtils.equals(cacheInfo.getAccountId(), accountId)) {
                resultObj = organizationsService.unbindAgent(organizeId, institutionInfo.getNumber(), cacheInfo.getAccountId(), "");
                if (resultObj.containsKey("errCode")) {
                    log.error("外部机构解绑经办人信息异常，{}", resultObj);
                }
            }
//            boolean flag = true;
//            JSONObject agentAccountObj = organizationsService.queryAccounts("", req.getContactIdCard());
//            log.info("查询外部用户【{}】接口响应{}", accountId, agentAccountObj);
//            if (null == agentAccountObj.getString("accountId")){
//                flag = false;
//            }
//            if (agentAccountObj.containsKey("errCode")) {
//                if ("-1".equals(agentAccountObj.getString("errCode"))) {
//                    flag = false;
//                } else {
//                    log.error("查询外部用户（联系人）信息异常，{}", agentAccountObj);
//                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), agentAccountObj.getString("msg"));
//                }
//            }
//            if (!flag) { //不存在则创建用户
//                JSONObject jsonObject = organizationsService.createAccounts(req.getContactName(), req.getContactIdCard(), req.getContactPhone());
//                if (jsonObject.containsKey("errCode")) {
//                    log.error("创建外部用户（联系人）信息异常，{}", jsonObject);
//                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), jsonObject.getString("msg"));
//                }
//            }
            resultObj = organizationsService.bindAgent(organizeId, institutionInfo.getNumber(), accountId, institutionInfo.getContactIdCard());
            if (resultObj.containsKey("errCode")) {
                log.error("外部机构绑定经办人信息异常，{}", resultObj);
            }
        }
        institutionInfo.setOrganizeId(organizeId);
        // 4>机构创建完成以后，更新数据库
        updateInstitutionInfo(institutionInfo);
        addYbInstitutionInfoChange(change);
        return new ApiResponse(ErrorCodeEnum.SUCCESS);
    }

    /**
     * 更新本地机构信息
     *
     * @param institutionInfo
     */
    private void updateInstitutionInfo(InstitutionInfo institutionInfo) {
        YbInstitutionInfo ybInstitutionInfo = new YbInstitutionInfo();
        BeanUtils.copyProperties(institutionInfo, ybInstitutionInfo);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        institutionInfo.setUpdateTime(df.format(new Date()));
        log.error("更新机构信息：{}", JSONObject.toJSONString(ybInstitutionInfo));
        institutionInfoMapper.updateById(ybInstitutionInfo);
    }

    @Override
    public void addYbInstitutionInfoChange(YbInstitutionInfoChange ybInstitutionInfoChange) {

        ybInstitutionInfoChangeMapper.insert(ybInstitutionInfoChange);
    }

    @Override
    public ApiResponse getInstitutionInfoChangeList(YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq) {
        //机构名称跟机构编号非空判断
        String number = ybInstitutionInfoChangeReq.getNumber();
        String institutionName = ybInstitutionInfoChangeReq.getInstitutionName();
        if (!StringUtils.isEmpty(number) || !StringUtils.isEmpty(institutionName)) {
            Integer pageNum = ybInstitutionInfoChangeReq.getPageNum();
            ybInstitutionInfoChangeReq.setPageNum((pageNum - 1) * ybInstitutionInfoChangeReq.getPageSize());

            List<YbInstitutionInfoChange> YbInstitutionInfoChangeList = ybInstitutionInfoChangeMapper.selectChangeList(ybInstitutionInfoChangeReq);

            List<YbInstitutionInfoChange> resList = new ArrayList<>();
            if (YbInstitutionInfoChangeList.size() > 0) {
                YbInstitutionInfoChangeList.forEach(c -> {

                    if (StringUtils.isNotBlank(c.getLicensePicture())) {
                        JSONArray xkzJson = JSONObject.parseArray(c.getLicensePicture());
                        List<String> xkzList = new ArrayList<>();
                        if (!Objects.isNull(xkzJson) && !xkzJson.isEmpty()) {
//                        log.info("jsonArray={}", xkzJson);
                            for (int i = 0; i < xkzJson.size(); i++) {
                                if (StringUtils.isNotBlank(xkzJson.getString(i))) {
//                                    String base64 = PicUploadUtil.getBase64(xkzJson.getString(i));
//                                    xkzList.add(base64);
                                    xkzList.add(ftpUploadUtil.uploadPath + xkzJson.getString(i));
                                }
                            }
                        }
                        c.setLicensePicture(JSONObject.toJSONString(xkzList));
                    } else {
                        c.setLicensePicture("");
                    }

                    if (StringUtils.isNotBlank(c.getBusinessPicture())) {
                        JSONArray yyzzJson = JSONObject.parseArray(c.getBusinessPicture());
                        List<String> yyzzList = new ArrayList<>();
                        if (!Objects.isNull(yyzzJson) && !yyzzJson.isEmpty()) {
                            for (int i = 0; i < yyzzJson.size(); i++) {
                                if (StringUtils.isNotBlank(yyzzJson.getString(i))) {
                                    yyzzList.add(ftpUploadUtil.uploadPath + yyzzJson.getString(i));
                                }
                            }
                        }
                        c.setBusinessPicture(JSONObject.toJSONString(yyzzList));
                    } else {
                        c.setBusinessPicture("");
                    }
                    resList.add(c);
                });
                Integer ybInstitutionInfoChangeCount = ybInstitutionInfoChangeMapper.selectChangeCount(ybInstitutionInfoChangeReq);
                Page<YbInstitutionInfoChange> page = new Page<>();
                page.setRecords(resList);
                page.setTotal(ybInstitutionInfoChangeCount);

                return new ApiResponse(page);

            }
            return new ApiResponse("200", "空");
        }
        return new ApiResponse("300", "机构编号或机构名称为空!");
    }

    @Override
    public void exportExcel(YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq, HttpServletResponse response) {
        String number = ybInstitutionInfoChangeReq.getNumber();
        String institutionName = ybInstitutionInfoChangeReq.getInstitutionName();


        if (!StringUtils.isEmpty(number) || !StringUtils.isEmpty(institutionName)) {
            List<YbInstitutionInfoChange> YbInstitutionInfoChangeList = ybInstitutionInfoChangeMapper.selectexportChangeList(ybInstitutionInfoChangeReq);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            int rowIndex = 0;
            int colIndex = 0;
            //表头
            String[] headers = {"机构编号", "机构名称", "统一社会信用代码", "机构法人姓名"
                    , "机构法人证件类型", "机构法人证件号", "机构法人手机号", "经办人姓名"
                    , "经办人证件类型", "经办人证件号", "经办人手机号", "修改时间"};

            XSSFRow headerRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i];
                XSSFCell headerRowCell = headerRow.createCell(i);
                headerRowCell.setCellValue(header);
                // 设置列的宽度
                sheet.setColumnWidth(i, 30 * 256);
            }
            for (YbInstitutionInfoChange ybInstitutionInfoChange : YbInstitutionInfoChangeList) {
                XSSFRow bodyRow = sheet.createRow(rowIndex++);
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getNumber());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getInstitutionName());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getOrgInstitutionCode());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalName());
                bodyRow.createCell(colIndex++).setCellValue("身份证");
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalIdCard());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalPhone());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactName());
                bodyRow.createCell(colIndex++).setCellValue("身份证");
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactIdCard());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactPhone());

                Date updateTime = ybInstitutionInfoChange.getUpdateTime();
                System.out.println(updateTime);
                bodyRow.createCell(colIndex++).setCellValue(sdf.format(updateTime));
                //将下标还原供下次循环使用
                colIndex = 0;
            }
            ExcelUtil.xlsDownloadFile(response, workbook);
        }
    }

    @Override
    public void exportExcel2(HttpServletResponse response) {
        List<YbInstitutionInfoChange> ybInstitutionInfoChanges = ybInstitutionInfoChangeMapper.selectList(null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        int rowIndex = 0;
        int colIndex = 0;
        //表头
        String[] headers = {"机构编号", "机构名称", "统一社会信用代码", "机构法人姓名"
                , "机构法人证件类型", "机构法人证件号", "机构法人手机号", "经办人姓名"
                , "经办人证件类型", "经办人证件号", "经办人手机号", "修改时间"};
        XSSFRow headerRow = sheet.createRow(rowIndex++);
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            XSSFCell headerRowCell = headerRow.createCell(i);
            headerRowCell.setCellValue(header);
            // 设置列的宽度
            sheet.setColumnWidth(i, 30 * 256);
        }
        for (YbInstitutionInfoChange ybInstitutionInfoChange : ybInstitutionInfoChanges) {
            XSSFRow bodyRow = sheet.createRow(rowIndex++);

            bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getNumber());
            bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getInstitutionName());
            bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getOrgInstitutionCode());
            bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalName());
            bodyRow.createCell(colIndex++).setCellValue("身份证");
            bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalIdCard());
            bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalPhone());
            bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactName());
            bodyRow.createCell(colIndex++).setCellValue("身份证");
            bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactIdCard());
            bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactPhone());
            Date updateTime = ybInstitutionInfoChange.getUpdateTime();

            bodyRow.createCell(colIndex++).setCellValue(sdf.format(updateTime));
            //将下标还原供下次循环使用
            colIndex = 0;
        }
        ExcelUtil.xlsDownloadFile(response, workbook);

    }

    @Override
    public ApiResponse getInstitutionInfobxList(InstitutionInfoQueryReq institutionInfoQueryReq, String token) {

        institutionInfoQueryReq.setPageNum((institutionInfoQueryReq.getPageNum() - 1) * institutionInfoQueryReq.getPageSize());
        List<YbInstitutionInfo> ybInstitutionInfos = institutionInfoMapper.getInstitutionInfobxList(institutionInfoQueryReq);
        if (ybInstitutionInfos.size() > 0) {
            Page<YbInstitutionInfo> page = new Page<>();
            page.setRecords(ybInstitutionInfos);
            page.setTotal(ybInstitutionInfos.size());
            return new ApiResponse(page);
        }
        List<YbOrgTd> YbOrgTdList = orgTdMapper.getorgTdbxList(institutionInfoQueryReq);
        List<YbInstitutionInfo> YbInstitutionInfolist = new ArrayList<>();
        if (YbOrgTdList.size() > 0) {
            YbOrgTdList.stream().forEach(x -> {
                YbInstitutionInfo ybInstitutionInfo = new YbInstitutionInfo();
                ybInstitutionInfo.setNumber(x.getAkb020());
                ybInstitutionInfo.setInstitutionName(x.getAkb021());
                YbInstitutionInfolist.add(ybInstitutionInfo);
            });
            Page<YbInstitutionInfo> page = new Page<>();
            page.setRecords(YbInstitutionInfolist);
            page.setTotal(YbOrgTdList.size());
            return new ApiResponse(page);


        }
        return new ApiResponse("200", "无保险公司");
    }

    @Override
    @LogAnnotation
    public ApiResponse newUpdateInstitutionInfo(InstitutionInfoAddReq req) {
        log.info("-------------------------------------------------------");
        String number = req.getNumber();
        // 查询机构是否在可访问范围
        QueryWrapper<YbOrgTd> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("AKB020", number);
        YbOrgTd orgTd = orgTdMapper.selectOne(queryWrapper);
        if (null == orgTd) {
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "机构不存在!");
        }

        // 查询机构是否在库，不在库则创建，否则更新
        YbInstitutionInfo local = this.getInstitutionInfo(number);
        if (local == null) {
            return insertInstitution(req, orgTd.getAkb021());
        }
        return updateInstitution(req, local, orgTd.getAkb021());
    }

    /**
     * 更新机构
     *
     * @param req
     * @param local
     * @param institutionName
     * @return
     */
    private ApiResponse updateInstitution(
            final InstitutionInfoAddReq req, final YbInstitutionInfo local, String institutionName) {
        log.info("[更新机构] 开始 {}", institutionName);

        // 待更新信息
        InstitutionInfo updateInfo = new InstitutionInfo();
        BeanUtils.copyProperties(req, updateInfo);
        updateInfo.setInstitutionName(institutionName);
        updateInfo.setOrganizeId(local.getOrganizeId());

        // 查询法人信息，若已存在则更新，否则创建
        ApiResponse legalAccountResp = findAccount(req.getLegalIdCard(), req.getLegalPhone());
        if (legalAccountResp.getData() != null) {
            // 查询已存在的法人信息，并更新
            JSONObject data = (JSONObject) legalAccountResp.getData();
            JSONObject resultObj = organizationsService.updateAccounts(
                    data.getString("accountId"), req.getLegalName(), req.getLegalIdCard(), req.getLegalPhone());
            if (resultObj.containsKey("errCode")) {
                log.error("更新外部用户（法人）信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            log.info("[更新机构] 查询并更新法人信息 {}", resultObj);
            updateInfo.setLegalAccountId(data.getString("accountId"));
        } else {
            // 创建法人信息
            JSONObject createAccount = organizationsService.createAccounts(
                    req.getLegalName(), req.getLegalIdCard(), req.getLegalPhone());
            if (createAccount.containsKey("errCode")) {
                log.error("创建外部用户（法人）信息异常，{}", createAccount);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), createAccount.getString("msg"));
            }
            log.info("[更新机构] 创建法人信息 {}", createAccount);
            updateInfo.setLegalAccountId(createAccount.getString("accountId"));
        }

        // 查询经办人信息，若已存在则更新，否则创建
        ApiResponse accountResp = findAccount(req.getContactIdCard(), req.getContactPhone());
        if (accountResp.getData() != null) {
            // 查询已存在的经办人信息，并更新
            JSONObject data = (JSONObject) accountResp.getData();
            JSONObject resultObj = organizationsService.updateAccounts(
                    data.getString("accountId"), req.getContactName(), req.getContactIdCard(), req.getContactPhone());
            if (resultObj.containsKey("errCode")) {
                log.error("更新外部用户（经办人）信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            log.info("[更新机构] 查询并更新经办人信息 {}", resultObj);
            updateInfo.setAccountId(data.getString("accountId"));
        } else {
            // 创建经办人信息
            JSONObject createAccount = organizationsService.createAccounts(
                    req.getContactName(), req.getContactIdCard(), req.getContactPhone());
            if (createAccount.containsKey("errCode")) {
                log.error("创建外部用户（经办人）信息异常，{}", createAccount);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), createAccount.getString("msg"));
            }
            log.info("[更新机构] 创建经办人信息 {}", createAccount);
            updateInfo.setAccountId(createAccount.getString("accountId"));
        }

        // 更新外部机构
        JSONObject resultObj = organizationsService.updateOrgans(updateInfo);
        if (resultObj.containsKey("errCode")) {
            log.error("更新外部机构信息异常，{}", resultObj);
            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
        }

        // 法人信息变更，将新法人绑定为经办人，原法人解绑
        if (!StringUtils.equals(local.getLegalAccountId(), updateInfo.getLegalAccountId())) {
            JSONObject bindResult = organizationsService.bindAgent(
                    updateInfo.getOrganizeId(), updateInfo.getNumber(), updateInfo.getLegalAccountId(), null, "1");
            if (bindResult.containsKey("errCode")) {
                log.error("外部机构将法人绑定为默认经办人异常，{}", bindResult);
            }
            JSONObject unbindResult = organizationsService.unbindAgent(
                    updateInfo.getOrganizeId(), updateInfo.getNumber(), local.getLegalAccountId(), null);
            if (unbindResult.containsKey("errCode")) {
                log.error("外部机构解绑法人异常，{}", unbindResult);
            }
        }

        // 经办人信息变更，将新经办人绑定，原经办人解绑
        if (!StringUtils.equals(local.getAccountId(), updateInfo.getAccountId())) {
            JSONObject bindResult = organizationsService.bindAgent(
                    updateInfo.getOrganizeId(), updateInfo.getNumber(), updateInfo.getAccountId(), null);
            if (bindResult.containsKey("errCode")) {
                log.error("外部机构绑定经办人异常，{}", bindResult);
            }
            JSONObject unbindResult = organizationsService.unbindAgent(
                    updateInfo.getOrganizeId(), updateInfo.getNumber(), local.getAccountId(), null);
            if (unbindResult.containsKey("errCode")) {
                log.error("外部机构解绑经办人异常，{}", unbindResult);
            }
        }

        // 更新本地机构
        updateInstitutionInfo(updateInfo);

        // 添加变更记录
        YbInstitutionInfoChange changeInfo =
                JSONObject.parseObject(JSONObject.toJSONString(updateInfo), YbInstitutionInfoChange.class);
        addYbInstitutionInfoChange(changeInfo);

        log.info("[更新机构] 结束 {}", institutionName);
        return ApiResponse.success();
    }

    /**
     * 新增机构
     *
     * @param req
     * @return
     */
    private ApiResponse insertInstitution(final InstitutionInfoAddReq req, String institutionName) {
        log.info("[新增机构] 开始 {}", institutionName);
        // 待存储信息
        InstitutionInfo saveInfo = new InstitutionInfo();
        BeanUtils.copyProperties(req, saveInfo);
        saveInfo.setInstitutionName(institutionName);

        // 查询或创建法人信息，获取法人的accountId
        ApiResponse legalAccountResp = findAccount(req.getLegalIdCard(), req.getLegalPhone());
        if (legalAccountResp.getData() != null) {
            // 查询已存在的法人信息
            JSONObject data = (JSONObject) legalAccountResp.getData();
            // 使用查询的法人信息比对入参的信息，若不一致则返回校验失败
            if (!StringUtils.equals(data.getString("licenseNumber"), req.getLegalIdCard())
                    || !StringUtils.equals(data.getString("mobile"), req.getLegalPhone())) {
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), "ERR-P：法人身份信息校验失败");
            }
            log.info("[新增机构] 查询到法人信息 {}", data);
            saveInfo.setLegalAccountId(data.getString("accountId"));
        } else {
            // 创建法人信息
            JSONObject createAccount = organizationsService.createAccounts(
                    req.getLegalName(), req.getLegalIdCard(), req.getLegalPhone());
            if (createAccount.containsKey("errCode")) {
                log.error("创建外部用户（法人）信息异常，{}", createAccount);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), createAccount.getString("msg"));
            }
            log.info("[新增机构] 创建法人信息 {}", createAccount);
            saveInfo.setLegalAccountId(createAccount.getString("accountId"));
        }

        // 查询或创建经办人信息，获取经办人的accountId
        ApiResponse accountResp = findAccount(req.getContactIdCard(), req.getContactPhone());
        if (accountResp.getData() != null) {
            // 查询已存在的经办人信息
            JSONObject data = (JSONObject) accountResp.getData();
            // 使用查询的经办人信息比对入参的信息，若不一致则返回校验失败
            if (!StringUtils.equals(data.getString("licenseNumber"), req.getContactIdCard())
                    || !StringUtils.equals(data.getString("mobile"), req.getContactPhone())) {
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), "ERR-P：经办人身份信息校验失败");
            }
            log.info("[新增机构] 查询到经办人信息 {}", data);
            saveInfo.setAccountId(data.getString("accountId"));
        } else {
            // 创建经办人信息
            JSONObject createAccount = organizationsService.createAccounts(
                    req.getContactName(), req.getContactIdCard(), req.getContactPhone());
            if (createAccount.containsKey("errCode")) {
                log.error("创建外部用户（经办人）信息异常，{}", createAccount);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), createAccount.getString("msg"));
            }
            log.info("[新增机构] 创建经办人信息 {}", createAccount);
            saveInfo.setAccountId(createAccount.getString("accountId"));
        }

        // 创建外部机构
        JSONObject createOrgan = organizationsService.createOrgans(saveInfo);
        if (createOrgan.containsKey("errCode")) {
            log.error("创建外部机构信息异常，{}", createOrgan);
            // 法人校验失败，则删除在库法人（避免出现不可达的错误数据）
            if (StringUtils.equals("法定代表人证件校验未通过", createOrgan.getString("msg"))) {
                organizationsService.deleteAccounts(saveInfo.getLegalAccountId());
            }
            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), createOrgan.getString("msg"));
        }
        saveInfo.setOrganizeId(createOrgan.getString("organizeId"));

        // 绑定法人为默认经办人
        JSONObject bindResult = organizationsService.bindAgent(
                saveInfo.getOrganizeId(), saveInfo.getNumber(), saveInfo.getLegalAccountId(), null, "1");
        if (bindResult.containsKey("errCode")) {
            log.error("外部机构将法人绑定为默认经办人信息异常，{}", bindResult);
        }

        // 存储本地机构
        YbInstitutionInfo institutionInfo = JSONObject.parseObject(JSONObject.toJSONString(saveInfo), YbInstitutionInfo.class);
        institutionInfoMapper.insert(institutionInfo);
        // 添加变更记录
        YbInstitutionInfoChange changeInfo =
                JSONObject.parseObject(JSONObject.toJSONString(institutionInfo), YbInstitutionInfoChange.class);
        addYbInstitutionInfoChange(changeInfo);

        log.info("[新增机构] 结束 {}", institutionName);
        return ApiResponse.success();
    }

    /**
     * 查询第三方用户accountId
     *
     * @param idCode
     * @param mobile
     * @return
     */
    private ApiResponse findAccount(String idCode, String mobile) {
        // 使用证件号查询accountId
        JSONObject result1 = organizationsService.listAccounts(idCode, null);
        // 接口调用错误，直接返回
        if (result1.containsKey("errCode") && !"-1".equals(result1.getString("errCode"))) {
            log.error("查询外部用户（法人）信息异常，证件号：{}，{}", idCode, result1);
            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), result1.getString("msg"));
        }
        // 返回列表不为空，则取第一个用户的accountId
        if (null != result1.getJSONArray("accounts") && result1.getJSONArray("accounts").size() > 0) {
            log.info("[查询外部用户] 证件号：{}，结果：{}", idCode, result1.toJSONString());
            return ApiResponse.success(result1.getJSONArray("accounts").get(0));
        }

        // 使用手机号查询accountId
        JSONObject result2 = organizationsService.listAccounts(null, mobile);
        // 接口调用错误，直接返回
        if (result2.containsKey("errCode") && !"-1".equals(result2.getString("errCode"))) {
            log.error("查询外部用户（法人）信息异常，手机号：{}，{}", result2);
            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), result2.getString("msg"));
        }
        // 返回列表不为空，则取第一个用户的accountId
        if (null != result2.getJSONArray("accounts") && result2.getJSONArray("accounts").size() > 0) {
            log.info("[查询外部用户] 手机号：{}，结果：{}", mobile, result2.toJSONString());
            return ApiResponse.success(result2.getJSONArray("accounts").getJSONObject(0));
        }
        log.info("[查询外部用户] 证件号：{}，手机号：{}，无结果", idCode, mobile);
        return ApiResponse.success();
    }

    /**
     * 通过列表查询yb_institution_info表中字段account_id | legal_account_id | organize_id都不为空的机构
     *
     * @param inputSet
     * @return
     */
    @Override
    public List<YbInstitutionInfo> findLegalInstitution(Set<String> inputSet) {
        QueryWrapper<YbInstitutionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("account_id");
        queryWrapper.isNotNull("legal_account_id");
        queryWrapper.isNotNull("organize_id");
        queryWrapper.in("number", inputSet);
        List<YbInstitutionInfo> list = institutionInfoMapper.selectList(queryWrapper);
        return list;
    }


    @Override
    public ApiResponse getArecordList(ArecordQueReq arecordQueReq) {

        arecordQueReq.setPageNum((arecordQueReq.getPageNum() - 1) * arecordQueReq.getPageSize());
        List<YbFlowInfo> YbFlowInfoList = ybFlowInfoMapper.selectYbFlowInfoList(arecordQueReq);
        if (YbFlowInfoList.size() > 0) {
            Page<YbFlowInfo> page = new Page<>();
            page.setRecords(YbFlowInfoList);
            page.setTotal(YbFlowInfoList.size());
            return new ApiResponse(page);
        }
        return new ApiResponse("200", "无符合条件");
    }

    @Override
    public void exportExcel3(ArecordQueReq arecordQueReq, HttpServletResponse response) {

        List<YbFlowInfo> YbFlowInfoList = ybFlowInfoMapper.selectExportYbFlowInfoList(arecordQueReq);
        Set<String> numberSet = new HashSet<>();
        YbFlowInfoList.forEach(y -> {
            numberSet.add(y.getNumber());
        });
        QueryWrapper<YbInstitutionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("number", numberSet);
        List<YbInstitutionInfo> ybInstitutionInfoList = ybInstitutionInfoMapper.selectList(queryWrapper);
        Map<String, YbInstitutionInfo> numberMap = new HashMap<>();
        ybInstitutionInfoList.forEach(y -> {
            numberMap.put(y.getNumber(), y);
        });
        log.info("numberMap = {}",JSONObject.toJSONString(numberMap));

        List<YbFlowDownload> res = new LinkedList<>();
        YbFlowInfoList.forEach(y -> {
            YbFlowDownload ybFlowDownload = new YbFlowDownload();
            ybFlowDownload.setNumber(y.getNumber());
            ybFlowDownload.setSignerType(y.getFlowName());
            YbInstitutionInfo ybInstitutionInfo = numberMap.get(y.getNumber());
            if(!Objects.isNull(ybInstitutionInfo)){
                BeanUtils.copyProperties(numberMap.get(y.getNumber()),ybFlowDownload);
            }
            res.add(ybFlowDownload);
        });

            log.info("List<YbFlowDownload> res = {}",JSONObject.toJSON(res));

        List<YbFlowDownload> list1 = new ArrayList();
        List<YbFlowDownload> list2 = new ArrayList();
        List<YbFlowDownload> list3 = new ArrayList();
        XSSFWorkbook excel = new XSSFWorkbook();
        for (YbFlowDownload ybFlowDownload : res) {
            if (ybFlowDownload.getSignerType() == null) {
                continue;
            }
            switch (ybFlowDownload.getSignerType()) {
                case "甲方":
                    list1.add(ybFlowDownload);
                    break;
                case "乙方":
                    list2.add(ybFlowDownload);
                    break;
                case "丙方":
                    list3.add(ybFlowDownload);
                    break;
                default:
                    break;
            }
        }
        ExcelUtil.exportExcel2(list1, excel, "甲方");
        ExcelUtil.exportExcel2(list2, excel, "乙方");
        ExcelUtil.exportExcel2(list3, excel, "丙方");
        ExcelUtil.xlsDownloadFile(response, excel);

    }

}
