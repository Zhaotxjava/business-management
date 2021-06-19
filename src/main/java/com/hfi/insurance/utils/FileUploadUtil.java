package com.hfi.insurance.utils;

/**
 * @Author ChenZX
 * @Date 2021/6/18 16:18
 * @Description:
 */
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Class Name: FileUploadUtil
 * Description: 文件上传下载工具类
 * @author:
 *
 */

@Component
@DependsOn({"applicationContextHelper", "fileUploadConstant"})
public final class FileUploadUtil {

    public static final Set<String> IMG_EXTS = Sets.newHashSet("jpg",
            "png", "jpeg", "gif", "bmp");
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

    private static final Set<String> VALID_FILE_EXTS = Sets.newHashSet("jpg",
            "png", "jpeg", "gif", "bmp", "pdf", "doc", "docx", "xls", "xlsx", "txt","zip","rar");
    public static final String FILE_NAME_LOG_TIP = "File name: {}";
    private static String uploadFilePath = null;
    private static String uploadFileUrl = null;

    private FileUploadUtil() {
    }

    /**
     * 文件上传，获取上传到的路径
     */
    @PostConstruct
    public void init() {
        FileUploadConstant  fileUploadConstant = (FileUploadConstant) ApplicationContextHelper.getBean("fileUploadConstant");
        uploadFilePath = fileUploadConstant.getUploadFilePath();
        uploadFileUrl = fileUploadConstant.getUploadFileUrl();
    }

    public static String getUploadFilePath() {
        return uploadFilePath;
    }

    public static String getUploadFileUrl() {
        return uploadFileUrl;
    }

    /**
     *
     * @Title: getFileExt
     * @Description: 获取文件扩展名
     * @param fileName 文件名
     * @return String
     */
    public static String getFileExt(String fileName) {
        return StringUtils.substringAfterLast(fileName, ".").toLowerCase();
    }

    /**
     * @Description: 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     */
    public static void deleteFile(String filePath) {
        try {
            File file = new File(getUploadFilePath(), filePath);
            FileUtils.forceDeleteOnExit(file);
        } catch (IOException e) {
            LOGGER.warn("delete file error", e);
        }
    }

    /**
     *
     * @Title: download
     * @Description: 文件下载
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param response HttpServletResponse
     */
    public static void download(String fileName, String filePath, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            //附件下载
            fileName = fileName.replaceAll("\r", "%0D");
            fileName = fileName.replaceAll("\n", "%0A");
            response.setHeader("content-disposition","attachment" + ";fileName="+ URLEncoder.encode(fileName,"UTF-8"));
            InputStream inputStream = getInputStream(filePath);
            ServletOutputStream outputStream = response.getOutputStream();
            //文件复制
            IOUtils.copy(inputStream, outputStream);
            response.flushBuffer();
        } catch (IOException e) {
            LOGGER.warn("download file error", e);
            throw new RuntimeException("文件下载失败");
        }
    }

    /**
     *
     * @Title: download
     * @Description: 文件下载
     * @param fileName 文件名
     * @param response HttpServletResponse
     */
    public static void download(InputStream inputStream, String fileName, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            //附件下载
            fileName = fileName.replaceAll("\r", "%0D");
            fileName = fileName.replaceAll("\n", "%0A");
            response.setHeader("content-disposition","attachment" + ";fileName="+ URLEncoder.encode(fileName,"UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            //文件复制
            IOUtils.copy(inputStream, outputStream);
            response.flushBuffer();
        } catch (IOException e) {
            LOGGER.warn("download file error", e);
            throw new RuntimeException("文件下载失败");
        }
    }

    /**
     *
     * @Title: getInputStream
     * @Description: 获取输入流
     * @param filePath 文件路径
     * @return InputStream
     * @throws FileNotFoundException 文件找不到异常
     */
    public static InputStream getInputStream(String filePath) throws FileNotFoundException {
        String realPath = getUploadFilePath() + "/" + filePath;
        return new FileInputStream(realPath);
    }


}

