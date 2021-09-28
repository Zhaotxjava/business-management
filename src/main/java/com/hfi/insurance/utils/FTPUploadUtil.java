package com.hfi.insurance.utils;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.enums.PicType;
import com.hfi.insurance.model.dto.PicCommitPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author ruicai
 * @description 文件或文件上传服务
 * @Date 2021/4/7 14:22
 */
@Slf4j
@Service
public class FTPUploadUtil {

    /**
     * 公网访问根路径
     */
    @Value("${uploadFile.path}")
    public String uploadPath;

    /**
     * 存储相对地址
     */
    @Value("${uploadFile.dir}")
    private String uploadDir;

    /**
     * FTP登录用户名
     */
    @Value("${uploadFile.userName}")
    private String userName;

    /**
     * FTP登录密码
     */
    @Value("${uploadFile.password}")
    private String password;

    /**
     * 内网主机IP
     */
    @Value("${uploadFile.host}")
    private String uploadHost;

    /**
     * 内网端口
     */
    @Value("${uploadFile.port}")
    private Integer uploadPort;
    @Value("${spring.profiles.active}")
    private String active;


    public ApiResponse cacheFile(MultipartFile file, String operationId, PicType type, String bumber) throws IOException {
        ApiResponse apiResponse = PicUploadUtil.checkOneFile(file);
        if (!apiResponse.isSuccess()) {
            return apiResponse;
        }
        PicCommitPath picCommit = PicUploadUtil.picCommitPath.get(operationId);
        if (Objects.isNull(picCommit)) {
            picCommit = new PicCommitPath();
//            log.info("{}" + JSONObject.toJSONString(picCommit));
        }
        String path = uploadFile(file, bumber);
        if (StringUtils.isBlank(path)) {
            log.info("上传文件到服务器失败了，operationId={}",operationId);
            return ApiResponse.fail(ErrorCodeEnum.FILE_UPLOAD_ERROR);
        }
//        String path = uploadOneFile();
        switch (type) {
            case XKZ:
                picCommit.getXkzList().add(path);
                break;
            case YYZZ:
                picCommit.getYyzzList().add(path);
                break;
            default:
                return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR, "图片类型非法");
        }
        PicUploadUtil.picCommitPath.put(operationId, picCommit);
        return ApiResponse.success(path);
    }

    /**
     * 上传文件
     *
     * @param multipartFile
     * @return
     */
    public String uploadFile(MultipartFile multipartFile, String number) throws IOException {
        log.info("[上传文件] 文件大小：" + multipartFile.getSize());

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(uploadHost, uploadPort);
        ftpClient.login(userName, password);
        ftpClient.changeWorkingDirectory(uploadDir);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//        ftpClient.enterRemotePassiveMode();

        if ("test".equals(active) || "dev".equals(active)) {
            //测试
            ftpClient.enterLocalActiveMode();
        } else if ("pro".equals(active) || "prod".equals(active)){
            //生产
            ftpClient.enterLocalPassiveMode();
        }


//        String fileName = multipartFile.getOriginalFilename();
//        fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
//        fileName = number+"_"+System.currentTimeMillis() + "." + fileName;
        String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        String dateName = df.format(calendar.getTime());
        String fileName = number + "_" + dateName + "_"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 15) + fileSuffix;
        log.info("文件上传 fileName={}", fileName);
        try (InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes())) {
            boolean b = ftpClient.storeFile(fileName, inputStream);
            ftpClient.logout();
            if (b) {
//                return uploadPath + fileName;
                return fileName;
            } else {
                return null;
            }
        }

    }

    /**
     * 批量上传文件
     *
     * @param multipartFiles
     * @return
     */
    public List<String> uploadFiles(MultipartFile[] multipartFiles) throws IOException {
        List<String> urls = new ArrayList<>();

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(uploadHost, uploadPort);
        ftpClient.login(userName, password);
        ftpClient.changeWorkingDirectory(uploadDir);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();

        for (int i = 0; i < multipartFiles.length; i++) {
            MultipartFile multipartFile = multipartFiles[i];
            log.info("[上传文件] {}/{} 文件大小：{}", i, multipartFiles.length, multipartFile.getSize());

            String fileName = multipartFile.getOriginalFilename();
            fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
            fileName = System.currentTimeMillis() + "." + fileName;

            try (InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes())) {
                ftpClient.storeFile(fileName, inputStream);
            }
            urls.add(uploadPath + fileName);
        }

        ftpClient.logout();

        return urls;
    }

    /**
     * 上传base64图片
     *
     * @param pics
     * @return
     * @throws IOException
     */
    public List<String> uploadPicsBase64(String[] pics) throws IOException {
        List<String> urls = new ArrayList<>();

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(uploadHost, uploadPort);
        ftpClient.login(userName, password);
        ftpClient.changeWorkingDirectory(uploadDir);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();

        for (int i = 0; i < pics.length; i++) {
            String fileName = System.currentTimeMillis() + ".jpg";
            log.info("[上传图片] {}/{}", i, pics.length);
            try (InputStream inputStream = new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(pics[i]))) {
                ftpClient.storeFile(fileName, inputStream);
            }
            urls.add(uploadPath + fileName);
        }

        ftpClient.logout();

        return urls;
    }

}
