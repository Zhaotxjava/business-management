package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.common.ExcelUtil;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.mapper.YbInstitutionInfoMapper;
import com.hfi.insurance.mapper.YbOrgTdMapper;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.YbOrgTd;
import com.hfi.insurance.model.dto.InstitutionInfoAddReq;
import com.hfi.insurance.model.dto.OrgTdQueryReq;
import com.hfi.insurance.model.dto.YbInstitutionInfoChangeReq;
import com.hfi.insurance.model.dto.res.InstitutionInfoRes;
import com.hfi.insurance.model.sign.BindedAgentBean;
import com.hfi.insurance.model.sign.QueryOuterOrgResult;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.OrganizationsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private YbInstitutionInfoChangeMapper  ybInstitutionInfoChangeMapper;


    @Override
    @LogAnnotation
    public ApiResponse getInstitutionInfoList(String token,String number, String institutionName, int current, int limit) {
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = requestAttributes.getRequest();
//        HttpSession session =  request.getSession();
//        String institutionNumber = (String) session.getAttribute("number");
        String jsonStr = caffeineCache.asMap().get(token);
        log.info("token:{}",token);
        if (StringUtils.isBlank(jsonStr)){
            return new ApiResponse(ErrorCodeEnum.TOKEN_EXPIRED.getCode(),ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String institutionNumber = jsonObject.getString("number");
        log.info("从token中获取机构编号：{}",institutionNumber);
//        if (StringUtils.isNotBlank(institutionNumber)){
//            queryWrapper.like("number",institutionNumber);
//        }
//        if (StringUtils.isNotBlank(number)){
//            queryWrapper.eq("number",number);
//        }
//        if (StringUtils.isNotBlank(institutionName)){
//            queryWrapper.eq("institution_name",institutionName);
//        }
        List<YbInstitutionInfo> ybInstitutionInfos = institutionInfoMapper.selectInstitutionInfoAndOrg(institutionNumber,number, institutionName, current-1, limit);
        int total = institutionInfoMapper.selectCountInstitutionInfoAndOrg(institutionNumber,number, institutionName);
        Page<YbInstitutionInfo> page = new Page<>(current,limit);
        page.setRecords(ybInstitutionInfos);
        page.setTotal(total);
        return new ApiResponse(page);
    }

    @Override
    @LogAnnotation
    public Page<InstitutionInfoRes> getOrgTdListForCreateFlow(OrgTdQueryReq req) {
        Integer pageNum = req.getPageNum();
        req.setPageNum(pageNum - 1);
        List<InstitutionInfoRes> ybInstitutionInfos = institutionInfoMapper.selectOrgForCreateFlow(req);
       //todo 添加保险公司
        int pageIndex = 1;
        int size = 1;
        List<InstitutionInfoRes> insuranceList = new ArrayList<>();
        List<QueryOuterOrgResult> queryOuterOrgResultList = new ArrayList<>();
        while (size > 0){
            String orgInfoListStr = organizationsService.queryByOrgName("",pageIndex);
            JSONObject object = JSONObject.parseObject(orgInfoListStr);
            if ("0".equals(object.getString("errCode"))) {
                String data = object.getString("data");
                List<QueryOuterOrgResult> queryOuterOrgResults = JSON.parseArray(data, QueryOuterOrgResult.class);
                if (0 == queryOuterOrgResults.size()){
                    size = 0;
                }
                pageIndex ++;
                queryOuterOrgResultList.addAll(queryOuterOrgResults);
            }else {
                break;
            }
        }
        log.info("外部机构数量：【{}】",queryOuterOrgResultList.size());
        for(QueryOuterOrgResult result : queryOuterOrgResultList){
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
        Page<InstitutionInfoRes> page = new Page<>(req.getPageNum(),req.getPageSize());
        page.setRecords(ybInstitutionInfos);
        page.setTotal(total);
        return page;
    }

    @Override
    @LogAnnotation
    public YbInstitutionInfo getInstitutionInfo(String number) {
        QueryWrapper<YbInstitutionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("number",number);
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
        queryWrapper.eq("AKB020",number);
        YbOrgTd orgTd = orgTdMapper.selectOne(queryWrapper);
        if (null == orgTd){
            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR.getCode(),"机构不存在!");
        }
        if (null == cacheInfo){
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
        // 2>通过天印系统查询联系人是否已存在于系统，不存在则调用创建用户接口，得到用户的唯一编码，存在则直接跳到第4步
        boolean accountExist = true;
        boolean organExist = true;
        boolean isSameAccount = true;  //法人和经办人是否同一个
        String accountId = "";
        String defaultAccountId = ""; //法人默认经办人
        String organizeId = "";
        if (!req.getLegalIdCard().equals(req.getContactIdCard())) {
            isSameAccount = false;
        }
        // 判定法人是否已存在系统用户
        log.info("机构信息：【{}】",JSON.toJSONString(cacheInfo));
        String legalAccountId = cacheInfo.getLegalAccountId() != null ? cacheInfo.getLegalAccountId() : "";
        JSONObject accountObj = organizationsService.queryAccounts(legalAccountId, "");
        log.info("查询外部用户【{}】接口响应{}", legalAccountId, accountObj);
        if (null == accountObj.getString("accountId")){
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
            if (StringUtils.isNotBlank(legalAccountId)){
                defaultAccountId = legalAccountId;
                JSONObject resultObj = organizationsService.updateAccounts(legalAccountId, req.getLegalName(), req.getLegalIdCard(), req.getLegalPhone());
                if (resultObj.containsKey("errCode")) {
                    log.error("更新外部用户（法人）信息异常，{}", resultObj);
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
                }
            }
        }

        boolean agentAccountExist = true;
        if (!isSameAccount) {
            log.info("法人和经办人信息不一致，再创建联系人为经办人");
            String agentAccountId = cacheInfo.getAccountId() != null ? cacheInfo.getAccountId() : "";
            //判断经办人（联系人）是否存在于系统内
            JSONObject agentAccountObj = organizationsService.queryAccounts(agentAccountId, "");
            log.info("查询外部用户【{}】接口响应{}", accountId, agentAccountObj);
            if (null == agentAccountObj.getString("accountId")){
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
            if (!agentAccountExist) { //不存在则创建用户
                JSONObject resultObj = organizationsService.createAccounts(req.getContactName(), req.getContactIdCard(), req.getContactPhone());
                if (resultObj.containsKey("errCode")) {
                    log.error("创建外部用户（联系人）信息异常，{}", resultObj);
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
                }
                accountId = resultObj.getString("accountId");
            } else {
                if (StringUtils.isNotBlank(agentAccountId)){
                    accountId = agentAccountId;
                    JSONObject resultObj = organizationsService.updateAccounts(agentAccountId, req.getContactName(), req.getContactIdCard(), req.getContactPhone());
                    if (resultObj.containsKey("errCode")) {
                        log.error("更新外部用户（联系人）信息异常，{}", resultObj);
                        return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
                    }
                }
            }
        }
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
        InstitutionInfo institutionInfo = new InstitutionInfo();
        BeanUtils.copyProperties(req, institutionInfo);
        institutionInfo.setInstitutionName(orgTd.getAkb021());
        if (!organExist) { //不存在则创建机构
            institutionInfo.setAccountId(defaultAccountId); //创建默认经办人
            institutionInfo.setLegalAccountId(defaultAccountId);
            JSONObject resultObj = organizationsService.createOrgans(institutionInfo);
            if (resultObj.containsKey("errCode")) {
                log.error("创建外部机构信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            organizeId = resultObj.getString("organizeId");
        } else {
            //更新机构信息
            organizeId = organObj.getString("organizeId");
            institutionInfo.setLegalAccountId(defaultAccountId);
            institutionInfo.setOrganizeId(organizeId);
            JSONObject resultObj = organizationsService.updateOrgans(institutionInfo);
            if (resultObj.containsKey("errCode")) {
                log.error("更新外部用户信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            //todo 法人信息变更，将法人信息添加为经办人
            if (!StringUtils.equals(defaultAccountId,organObj.getString("agentAccountId"))){
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
        }else {
            accountId = defaultAccountId;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        institutionInfo.setAccountId(accountId);
        institutionInfo.setLegalAccountId(defaultAccountId);
        institutionInfo.setOrganizeId(organizeId);
        institutionInfo.setUpdateTime(df.format(new Date()));
        // 4>机构创建完成以后，更新数据库
        YbInstitutionInfo ybInstitutionInfo = new YbInstitutionInfo();
        BeanUtils.copyProperties(institutionInfo,ybInstitutionInfo);
        institutionInfoMapper.updateById(ybInstitutionInfo);
        return new ApiResponse(ErrorCodeEnum.SUCCESS);
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
        if (!StringUtils.isEmpty(number) || !StringUtils.isEmpty(institutionName)){
            Integer pageNum = ybInstitutionInfoChangeReq.getPageNum();
            ybInstitutionInfoChangeReq.setPageNum(pageNum-1);
            List<YbInstitutionInfoChange>  YbInstitutionInfoChangeList= ybInstitutionInfoChangeMapper.selectChangeList(ybInstitutionInfoChangeReq);
            if (YbInstitutionInfoChangeList.size()>0){
                return new ApiResponse(YbInstitutionInfoChangeList);
            }
            return new ApiResponse("200","空");
        }
        return new ApiResponse("300","机构编号或机构名称为空!");
    }

    @Override
    public void exportExcel(YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq,HttpServletResponse response) {
        String number = ybInstitutionInfoChangeReq.getNumber();
        String institutionName = ybInstitutionInfoChangeReq.getInstitutionName();


        if (!StringUtils.isEmpty(number) || !StringUtils.isEmpty(institutionName)){

            List<YbInstitutionInfoChange>  YbInstitutionInfoChangeList= ybInstitutionInfoChangeMapper.selectChangeList(ybInstitutionInfoChangeReq);
            SimpleDateFormat   sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            int rowIndex = 0;
            int colIndex = 0;
            //表头
            String[] headers = {"id","机构编号","机构名称","统一社会信用代码","机构法人姓名"
                    ,"机构法人证件类型","机构法人证件号","机构法人手机号","经办人姓名"
                    ,"经办人证件类型","经办人证件号","经办人手机号","天印系统经办人用户标识"
                    ,"天印系统法人用户标识","天印系统机构标记","修改时间"};
            XSSFRow headerRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i];
                XSSFCell headerRowCell = headerRow.createCell(i);
                headerRowCell.setCellValue(header);
                // 设置列的宽度
                sheet.setColumnWidth(i, 30*256);
            }
            for (YbInstitutionInfoChange ybInstitutionInfoChange : YbInstitutionInfoChangeList) {
                XSSFRow bodyRow = sheet.createRow(rowIndex++);
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getId());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getNumber());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getInstitutionName());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getOrgInstitutionCode());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalName());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalCardType());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalIdCard());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalPhone());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactName());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactCardType());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactIdCard());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getContactPhone());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getAccountId());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getLegalAccountId());
                bodyRow.createCell(colIndex++).setCellValue(ybInstitutionInfoChange.getOrganizeId());
                Date updateTime = ybInstitutionInfoChange.getUpdateTime();
                System.out.println(updateTime);
                bodyRow.createCell(colIndex++).setCellValue(sdf.format(updateTime));
                //将下标还原供下次循环使用
                colIndex=0;
            }
            ExcelUtil.xlsDownloadFile(response,workbook);
        }
    }

    @SneakyThrows
    @Override
    public void exportExcel2(HttpServletResponse response) {
        List<YbInstitutionInfoChange> ybInstitutionInfoChanges = ybInstitutionInfoChangeMapper.selectList(null);
        ExcelUtil.exportExcel(ybInstitutionInfoChanges,response);

    }


}
