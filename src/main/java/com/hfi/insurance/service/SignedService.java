package com.hfi.insurance.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionV2Model;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.model.sign.req.StandardCreateFlowBO;
import com.hfi.insurance.model.sign.req.TemplateUseParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author ChenZX
 * @Date 2021/7/1 17:18
 * @Description:
 */
public interface SignedService {

    /**
     * 分页查询模板信息列表，根据机构分页查询有权限的且已发布的模板
     * @param getPageWithPermissionV2Model
     * @return
     */
    JSONObject getPageWithPermission(GetPageWithPermissionV2Model getPageWithPermissionV2Model);

    /**
     * 获取文档模板的详细信息
     * @param templateId
     * @return
     */
    JSONObject getTemplateInfo(String templateId);

    /**
     * 使用文档模板同步生成pdf文件
     * @param templateUseParam
     * @return
     */
    JSONObject buildTemplateDoc(TemplateUseParam templateUseParam);

    JSONObject upload(MultipartFile file, String fileName);

    /**
     * 文件直传，返回fileKey
     * @param file
     * @return
     */
    JSONObject upload(MultipartFile file);


    /**
     * 创建流程
     * @param standardCreateFlow
     * @return
     */

    JSONObject createSignFlows(StandardCreateFlowBO standardCreateFlow);

    JSONObject getDocKeyPosition();

    /**
     * 获取签署地址列表
     * @param req
     * @return
     */
    JSONObject getSignUrls(GetSignUrlsReq req);

    /**
     * 获取签署流程进度详情
     * @param signFlowId
     * @return
     */
    JSONObject getSignDetail(Integer signFlowId);

    /**
     * 获取签署流程文档下载地址
     * @param signFlowId
     * @return
     */
    JSONObject getSignFlowDocUrls(String signFlowId);

    /**
     * 获取文档预览的URL
     * @param fileKey
     * @param docId
     * @return
     */
    JSONObject getPreviewUrl(String fileKey, String docId);

    /**
     * 查询内部机构印章列表
     * @param organizeId
     * @param organizeNo
     * @return
     */
    JSONObject getInnerOrgansSeals(String organizeId,String organizeNo);

    /**
     * 查询内部印章详细数据
     * @param sealId
     * @return
     */
    JSONObject getSealInfos(String sealId);


    JSONObject getDownloadUrl(String docId, String fileKey);

    ApiResponse isResultSuccess(JSONObject result);
}
