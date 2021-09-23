package com.hfi.insurance.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String uploadPath;

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

    /**
     * 上传文件
     * @param multipartFile
     * @return
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        log.info("[上传文件] 文件大小：" + multipartFile.getSize());

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(uploadHost, uploadPort);
        ftpClient.login(userName, password);
        ftpClient.changeWorkingDirectory(uploadDir);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        String fileName = multipartFile.getOriginalFilename();
        fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
        fileName = System.currentTimeMillis() + "." + fileName;

        try (InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes())) {
            ftpClient.storeFile(fileName, inputStream);
        }

        ftpClient.logout();

        return uploadPath + fileName;
    }

    /**
     * 批量上传文件
     * @param multipartFiles
     * @return
     */
    public List<String> uploadFiles(MultipartFile[] multipartFiles) throws IOException {
        List<String> urls = new ArrayList<>();

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(uploadHost,uploadPort);
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
     * @param pics
     * @return
     * @throws IOException
     */
    public List<String> uploadPicsBase64(String[] pics) throws IOException {
        List<String> urls = new ArrayList<>();

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(uploadHost,uploadPort);
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
