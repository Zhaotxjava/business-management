//package com.hfi.insurance.utils;
//
///**
// * @Author ChenZX
// * @Date 2021/6/18 16:18
// * @Description:
// */
//import com.google.common.collect.Sets;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.client.utils.DateUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.PostConstruct;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URLEncoder;
//import java.util.Date;
//import java.util.Set;
//import java.util.UUID;
//
///**
// * Class Name: FileUploadUtil
// * Description: 文件上传下载工具类
// * @author:
// *
// */
//
//@Component
//@DependsOn({"applicationContextHelper", "fileUploadConstant"})
//public final class FileUploadUtil {
//
//    public static final Set<String> IMG_EXTS = Sets.newHashSet("jpg",
//            "png", "jpeg", "gif", "bmp");
//    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);
//
//    private static final Set<String> VALID_FILE_EXTS = Sets.newHashSet("jpg",
//            "png", "jpeg", "gif", "bmp", "pdf", "doc", "docx", "xls", "xlsx", "txt","zip","rar");
//    public static final String FILE_NAME_LOG_TIP = "File name: {}";
//    private static String uploadFilePath = null;
//    private static String uploadFileUrl = null;
//
//    private FileUploadUtil() {
//    }
//
//    /**
//     * 文件上传，获取上传到的路径
//     */
//    @PostConstruct
//    public void init() {
//        FileUploadConstant  fileUploadConstant = (FileUploadConstant) ApplicationContextHelper.getBean("fileUploadConstant");
//        uploadFilePath = fileUploadConstant.getUploadFilePath();
//        uploadFileUrl = fileUploadConstant.getUploadFileUrl();
//    }
//
//    public static String getUploadFilePath() {
//        return uploadFilePath;
//    }
//
//    public static String getUploadFileUrl() {
//        return uploadFileUrl;
//    }
//
//    /**
//     *
//     * @Title: isImageFile
//     * @Description: 判断是否文件
//     * @param ext STRING
//     * @return boolean
//     */
//    public static boolean isImageFile(String ext) {
//        return IMG_EXTS.contains(ext);
//    }
//
//    /**
//     * @Description 文件上传
//     * @param file 文件
//     * @return FileDto
//     * @throws BizServiceException 业务异常
//     */
//    public static SysFileDto upload(File file) throws BizServiceException {
//        return uploadFile(file, false);
//    }
//    /**
//     * @Description 文件上传并加水印
//     * @param file 文件
//     * @return FileDto
//     * @throws BizServiceException 业务异常
//     * @throws IOException 读写异常
//     */
//    public static SysFileDto uploadAndWaterMark(File file)throws BizServiceException {
//        return uploadFile(file, true);
//    }
//    /**
//     * @Description 文件上传
//     * @param file 文件
//     * @return FileDto
//     * @throws BizServiceException 业务异常
//     */
//    public static SysFileDto upload(MultipartFile file) throws BizServiceException {
//        return uploadMultipartFile(file, false);
//    }
//    /**
//     * @Description 文件上传并加水印
//     * @param file 文件 MultipartFile
//     * @return FileDto
//     * @throws BizServiceException 业务异常
//     */
//    public static SysFileDto uploadAndWaterMark(MultipartFile file)throws BizServiceException {
//        return uploadMultipartFile(file, true);
//    }
//
//    /**
//     *
//     * @Title: getTempFile
//     * @Description: 获取临时文件
//     * @param file MultipartFile
//     * @return File
//     */
//    public static File getTempFile(MultipartFile file) {
//        try {
//            return  new GetFileImpl().apply(file, false);
//        } catch (IOException e) {
//            LOGGER.info(e.getMessage());
//        }
//        return null;
//    }
//
//    /**
//     *
//     * @Title: getFileExt
//     * @Description: 获取文件扩展名
//     * @param fileName 文件名
//     * @return String
//     */
//    public static String getFileExt(String fileName) {
//        return StringUtils.substringAfterLast(fileName, ".").toLowerCase();
//    }
//
//    /**
//     * @Description: 删除单个文件
//     *
//     * @param filePath 被删除文件的文件名
//     */
//    public static void deleteFile(String filePath) {
//        try {
//            File file = new File(getUploadFilePath(), filePath);
//            FileUtils.forceDeleteOnExit(file);
//        } catch (IOException e) {
//            LOGGER.warn("delete file error", e);
//            throw new BizServiceException();
//        }
//    }
//
//    /**
//     *
//     * @Title: download
//     * @Description: 文件下载
//     * @param fileName 文件名
//     * @param filePath 文件路径
//     * @param response HttpServletResponse
//     */
//    public static void download(String fileName, String filePath, HttpServletResponse response) {
//        try {
//            response.setContentType("text/html;charset=UTF-8");
//            response.setContentType("application/octet-stream");
//            //附件下载
//            fileName = fileName.replaceAll("\r", "%0D");
//            fileName = fileName.replaceAll("\n", "%0A");
//            response.setHeader("content-disposition","attachment" + ";fileName="+ URLEncoder.encode(fileName,"UTF-8"));
//            InputStream inputStream = getInputStream(filePath);
//            ServletOutputStream outputStream = response.getOutputStream();
//            //文件复制
//            IOUtils.copy(inputStream, outputStream);
//            response.flushBuffer();
//        } catch (IOException e) {
//            LOGGER.warn("download file error", e);
//            throw new BizServiceException("文件下载失败");
//        }
//    }
//
//    /**
//     *
//     * @Title: download
//     * @Description: 文件下载
//     * @param fileName 文件名
//     * @param response HttpServletResponse
//     */
//    public static void download(InputStream inputStream, String fileName, HttpServletResponse response) {
//        try {
//            response.setContentType("text/html;charset=UTF-8");
//            response.setContentType("application/octet-stream");
//            //附件下载
//            fileName = fileName.replaceAll("\r", "%0D");
//            fileName = fileName.replaceAll("\n", "%0A");
//            response.setHeader("content-disposition","attachment" + ";fileName="+ URLEncoder.encode(fileName,"UTF-8"));
//            ServletOutputStream outputStream = response.getOutputStream();
//            //文件复制
//            IOUtils.copy(inputStream, outputStream);
//            response.flushBuffer();
//        } catch (IOException e) {
//            LOGGER.warn("download file error", e);
//            throw new BizServiceException("文件下载失败");
//        }
//    }
//
//    /**
//     *
//     * @Title: getInputStream
//     * @Description: 获取输入流
//     * @param filePath 文件路径
//     * @return InputStream
//     * @throws FileNotFoundException 文件找不到异常
//     */
//    public static InputStream getInputStream(String filePath) throws FileNotFoundException {
//        String realPath = getUploadFilePath() + "/" + filePath;
//        return new FileInputStream(realPath);
//    }
//
//    private static SysFileDto uploadMultipartFile(MultipartFile file, boolean mask) {
//        String ext = getFileExt(file.getOriginalFilename());
//        checkFileExt(ext);
//        String fileName = generateFileName(ext);
//        StringBuilder sb = new StringBuilder();
//        sb.append(generateRelativePath());
//        logMultipartFileInfo(file, fileName);
//        String filePath = null;
//        try {
//            File tempFile = new GetFileImpl().apply(file, mask);
//            File fileDir = new File(getUploadFilePath() + sb.toString());
//            if (!fileDir.exists()) {
//                fileDir.mkdirs();
//            }
//            sb.append(fileName);
//            filePath = sb.toString();
//            File targetFile = new File(getUploadFilePath() + filePath);
//            FileUtils.copyFile(tempFile, targetFile);
//            tempFile.delete();
//        } catch (IOException e) {
//            LOGGER.warn("upload file error", e);
//            throw new BizServiceException("文件上传失败,请重新上传");
//        }
//        return populateMultipartFileDto(file, filePath);
//    }
//
//    private static SysFileDto uploadFile(File file, boolean mask) {
//        String ext = getFileExt(file.getName());
//        checkFileExt(ext);
//        String fileName = generateFileName(ext);
//        StringBuilder sb = new StringBuilder();
//        sb.append(generateRelativePath());
//        logFileInfo(file, fileName);
//        String filePath = null;
//        try {
//            File tempFile = new GetFileImpl().apply(file, mask);
//            File fileDir = new File(getUploadFilePath() + sb.toString());
//            if (!fileDir.exists()) {
//                fileDir.mkdirs();
//            }
//            sb.append(fileName);
//            filePath = sb.toString();
//            File targetFile = new File(getUploadFilePath() + filePath);
//            FileUtils.copyFile(tempFile, targetFile);
//            tempFile.delete();
//        } catch (IOException e) {
//            LOGGER.warn("upload file error", e);
//            throw new BizServiceException("文件上传失败，请重新上传");
//        }
//        return populateFileDto(file, filePath);
//    }
//
//    private static void checkFileExt(String ext) {
//        if (!VALID_FILE_EXTS.contains(ext)) {
//            throw new BizServiceException("上传文件类型不支持，请重新上传");
//        }
//    }
//
//    private static String generateFileName(String ext) {
//        Date currDate = new Date();
//        String times = DateUtils.formatDate(currDate, "hhmmss");
//        StringBuilder sb = new StringBuilder();
//        sb.append(UUID.randomUUID()).append(times).append(".").append(ext);
//        return sb.toString();
//    }
//
//    private static String generateRelativePath() {
//        Date currDate = new Date();
//        String year = DateUtils.formatDate(currDate, "yyyy");
//        String month = DateUtils.formatDate(currDate, "MM");
//        StringBuilder filePath = new StringBuilder();
//        String separator = "/";
//        filePath.append(year).append(separator);
//        filePath.append(month).append(separator);
//        return filePath.toString();
//    }
//
//    private static void logMultipartFileInfo(MultipartFile file, String fileName) {
//        LOGGER.debug(FILE_NAME_LOG_TIP, fileName);
//        LOGGER.debug(FILE_NAME_LOG_TIP, file.getName());
//        LOGGER.debug("File size: {}", file.getSize());
//        LOGGER.debug("File type: {}", file.getContentType());
//    }
//    private static void logFileInfo(File file, String fileName) {
//        LOGGER.debug(FILE_NAME_LOG_TIP, fileName);
//        LOGGER.debug(FILE_NAME_LOG_TIP, file.getName());
//        LOGGER.debug("File TotalSpace: {}", file.getTotalSpace());
//        LOGGER.debug("File UsableSpace: {}", file.getUsableSpace());
//    }
//
//    private static SysFileDto populateMultipartFileDto(MultipartFile file, String filePath) {
//        SysFileDto fileDto = new SysFileDto();
//        fileDto.setFileName(file.getOriginalFilename());
//        fileDto.setFilePath(filePath);
//        fileDto.setId(filePath);
//        //设置文件后缀名
//        fileDto.setFileExtName(getFileExt(file.getOriginalFilename()));
//        return fileDto;
//    }
//    private static SysFileDto populateFileDto(File file, String filePath) {
//        SysFileDto fileDto = new SysFileDto();
//        fileDto.setFileName(file.getName());
//        fileDto.setFilePath(filePath);
//        fileDto.setId(filePath);
//        fileDto.setFileExtName(getFileExt(file.getName()));
//        return fileDto;
//    }
//
//    private static class GetFileImpl implements GetFile {
//        @Override
//        public File apply(MultipartFile soruceFile, boolean mask) throws IOException {
//            String ext = getFileExt(soruceFile.getOriginalFilename());
//            String tempDirectoryPath = FileUtils.getTempDirectoryPath();
//            String tempFileName = UUIDUtils.createUUIDNoDash() + "." +ext;
//            File tempFile = new File(tempDirectoryPath + File.separator + tempFileName);
//            FileUtils.copyInputStreamToFile(soruceFile.getInputStream(), tempFile);
//            return tempFile;
//        }
//        @Override
//        public File apply(File soruceFile, boolean mask) throws IOException {
//            String ext = getFileExt(soruceFile.getName());
//            String tempDirectoryPath = FileUtils.getTempDirectoryPath();
//            String tempFileName = UUIDUtils.createUUIDNoDash() + "." + ext;
//            File tempFile = new File(tempDirectoryPath + File.separator + tempFileName);
//            FileUtils.copyInputStreamToFile(new FileInputStream(soruceFile), tempFile);
//            return tempFile;
//        }
//    }
//
//    private interface GetFile {
//        File apply(MultipartFile soruceFile, boolean mask) throws IOException;
//        File apply(File soruceFile, boolean mask) throws IOException;
//    }
//}
//
