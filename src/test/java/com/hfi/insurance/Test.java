package com.hfi.insurance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.utils.HmacSHA256Utils;
import com.hfi.insurance.utils.HttpUtil;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class Test {

    public static void main(String[] args) {
        testDoPostForOpenApi();
    }

    /**
     * 以请求外部用户详情接口为例，get请求没有请求体，所以生成秘钥的时候以空字符为原文生成对应的秘钥，也就是同一个项目ID的get请求的秘钥应该都是一样的;
     */
    public void testDoGetForOpenApi() {
        Map<String, String> heads = new HashMap<>();
        heads.put("x-timevale-project-id", "1000003");
        String signature = HmacSHA256Utils.hmacSha256("", "2ffb638e64e364103edf927411f087e4");
        heads.put("x-timevale-signature", signature);
        Map<String, String> params = new HashMap<>();
        params.put("accountId", "c205166f-2c1e-49cf-aa5e-c59312a60816");
        params.put("uniqueId", "zml001");
        String result = HttpUtil.doGet("http://127.0.0.1:8035/V1/accounts/outerAccounts/query", heads, params);
        System.out.println(result);
    }

    /**
     * 以添加外部用户接口为例，post请求需要根据请求体计算出对应的秘钥，然后作为头信息x-timevale-signature的值
     */
    public static void testDoPostForOpenApi() {
        Map<String, String> heads = new HashMap<>();
        heads.put("x-timevale-project-id", "1001001");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contactsEmail", "");
        jsonObject.put("contactsMobile", "18027384000");
        jsonObject.put("licenseNumber", "220181197708241552");
        jsonObject.put("licenseType", "IDCard");
        jsonObject.put("loginEmail", "");
        jsonObject.put("loginMobile", "18027384000");
        jsonObject.put("name", "陆立国");
        jsonObject.put("uniqueId", "220181197708241552");
        String signature = HmacSHA256Utils.hmacSha256(jsonObject.toJSONString(), "c5505d7831bc626e92c175052956c7fc");
        heads.put("x-timevale-signature", signature);
        String result = HttpUtil.doPost("http://page.iconntech.cn:18209/V1/accounts/outerAccounts/create", heads, jsonObject.toJSONString());
        System.out.println(result);
    }

    /**
     * 示例演示的是文件上传的过程，先通过post请求获取文件直传地址，然后通过文件直传地址上传文件
     *
     * @throws IOException
     */
    public void testDoPutForUploadFile() throws IOException {
        //读文档
        File file = new File("D:\\测试.pdf");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();

        //获取上传文档直传地址
        String md5 = "";//MD5Util.md5(data);
        int length = data.length;
        Map<String, String> heads = new HashMap<>();
        heads.put("x-timevale-project-id", "1000003");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contentLength", length);
        jsonObject.put("fileMD5", md5);
        jsonObject.put("docType", "Pdf");

        String signature = HmacSHA256Utils.hmacSha256(jsonObject.toJSONString(), "2ffb638e64e364103edf927411f087e4");
        heads.put("x-timevale-signature", signature);
        String result = HttpUtil.doPost("http://127.0.0.1:8035/esignpro/rest/filemanage/getUploadUrl", heads, jsonObject.toJSONString());

        //文件直传
        JSONObject resultObject = JSON.parseObject(result);
        JSONObject fileData = resultObject.getJSONObject("data");
        //文件filekey
        String fileKey = fileData.getString("fileKey");
        String url = fileData.getString("url");
        Map<String, String> putHeads = new HashMap<>();
        putHeads.put("Content-Type", "application/octet-stream");
        putHeads.put("Content-MD5", md5);
        String s = HttpUtil.doPut(url, putHeads, null, data);
        System.out.println(s);
    }

    /**
     * 该示例演示的是直接进行文件上传，不通过文件直传地址进行上传文件，文件上传接口加了白名单，无需计算秘钥
     *
     * @throws IOException
     */
    public void testDoPutForUploadFile2() throws IOException {
        //读文档
        File file = new File("D:\\测试.pdf");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();

        Map<String, String> heads = new HashMap<>();
        heads.put("x-timevale-project-id", "1000003");

        String result = HttpUtil.doPostFile("http://127.0.0.1:8035/V1/files/upload", data, "file", "劳动合同.pdf", heads, null);
        System.out.println(result);
    }
}