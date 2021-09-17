package com.hfi.insurance.utils;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.enums.PicType;
import com.hfi.insurance.model.PicPathRes;
import com.hfi.insurance.model.dto.PicCommit;
import com.hfi.insurance.model.dto.PicCommitPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jthealth-NZH
 * @Date 2021/9/8 15:23
 * @Describe
 * @Version 1.0
 */
@Slf4j
public class PicUploadUtil {

    public static Map<String, PicCommit> picCommitMap = new HashMap<>();
    public static Map<String, PicCommitPath> picCommitPath = new HashMap<>();

    public static ApiResponse<PicPathRes> uploadFiles2(MultipartFile[] xxk, MultipartFile[] yyzz, String dir,String number) {
        try {
            //创建文件在服务器端存放路径
//            String dir = request.getServletContext().getRealPath("/upload");
            log.info("入参文件数xxk：{}。yyzz：{}。开始上传文件到此路径：{}", xxk.length, yyzz.length, dir);
            if (xxk.length <= 0 && yyzz.length <= 0) {
                return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(), ErrorCodeEnum.RESPONES_ERROR.getMessage() + "，文件数量为空");
            }
            List<String> xkzList = new ArrayList<>();
            List<String> yyzzList = new ArrayList<>();

            uploadFilesHelper(xxk, xkzList, PicType.XKZ, dir,number);
            uploadFilesHelper(yyzz, yyzzList, PicType.YYZZ, dir,number);

            PicPathRes res = new PicPathRes();
            res.setXkzList(xkzList);
            res.setYyzzList(yyzzList);
            return ApiResponse.success(res);
        } catch (Exception e) {
            log.error("", e);
            return ApiResponse.fail("图片上传失败:{}", e.getMessage());
        }
    }

//    public static ApiResponse cacheFile(MultipartFile file, String operationId, String picType){
//        return cacheFile(file,operationId,picType,"");
//    }
    public static ApiResponse cacheFile(MultipartFile file, String operationId, String picType,String dir,String bumber) throws IOException {
        ApiResponse apiResponse = checkOneFile(file);
        if(!apiResponse.isSuccess()){
            return apiResponse;
        }
        PicCommitPath picCommit = picCommitPath.get(operationId);
        if (Objects.isNull(picCommit)) {
            picCommit = new PicCommitPath();
        }
        PicType type = PicType.getPicType(picType);
        String path = uploadOneFile(file,dir,type,bumber);
//        String path = uploadOneFile();
        switch (type){
            case XKZ:
                picCommit.getXkz().add(path);
                break;
            case YYZZ:
                picCommit.getYyzz().add(path);
                break;
            default:
                return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR,"图片类型非法");
        }
        picCommitPath.put(operationId,picCommit);
        return ApiResponse.success();
    }

//    public static String getImageStr(File file, String fileType) throws IOException {
//        String fileContentBase64 = null;
//        String base64Str = "data:" + fileType + ";base64,";
//        String content = null;
//        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
//        InputStream in = null;
//        byte[] data = null;
//        //读取图片字节数组
//        try {
//            in = new FileInputStream(file);
//            data = new byte[in.available()];
//            in.read(data);
//            in.close();
//            //对字节数组Base64编码
//            if (data == null || data.length == 0) {
//                return null;
//            }
//            content = Base64.encodeBytes(data);
//            if (content == null || "".equals(content)) {
//                return null;
//            }
//            fileContentBase64 = base64Str + content;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (in != null) {
//                in.close();
//            }
//        }
//        return fileContentBase64;
//    }

    public static ApiResponse cacheFileInFileSystem(MultipartFile file, String operationId, String picType,String dir,String bumber) throws IOException {
        ApiResponse apiResponse = checkOneFile(file);
        if(!apiResponse.isSuccess()){
            return apiResponse;
        }
        PicCommitPath picCommit = picCommitPath.get(operationId);
        if (Objects.isNull(picCommit)) {
            picCommit = new PicCommitPath();
        }
        PicType type = PicType.getPicType(picType);
        String path = uploadOneFile(file,dir,type,bumber);
        switch (type){
            case XKZ:
                picCommit.getXkz().add(path);
                break;
            case YYZZ:
                picCommit.getYyzz().add(path);
                break;
            default:
                return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR,"图片类型非法");
        }
        picCommitPath.put(operationId,picCommit);
        return ApiResponse.success();
    }


    public static ApiResponse<PicPathRes> fileCommit(String id,String dir) throws IOException {
        PicCommitPath picCommit = picCommitPath.get(id);
        if(Objects.isNull(picCommit)){
            log.info("fileCommit id = {},未检测到需要提交的图片",id);
            return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(),"未检测到需要提交的图片");
        }
//        List<String> xkz = new ArrayList<>();
//        List<String> yyzz = new ArrayList<>();
//        commitFileList(picCommit.getXkz(),xkz,dir,PicType.XKZ);
//        commitFileList(picCommit.getYyzz(),yyzz,dir,PicType.YYZZ);
        PicPathRes picPathRes = new PicPathRes();
        picPathRes.setXkzList(picCommit.getXkz());
        picPathRes.setYyzzList(picCommit.getYyzz());
        picCommitPath.remove(id);
        return ApiResponse.success(picPathRes);

    }

//    public static ApiResponse<PicPathRes> fileCommit2(String id,String dir) throws IOException {
//        PicCommit picCommit = picCommitMap.get(id);
//        if(Objects.isNull(picCommit)){
//            log.info("fileCommit id = {},未检测到需要提交的图片",id);
//            return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(),"未检测到需要提交的图片");
//        }
//        List<String> xkz = new ArrayList<>();
//        List<String> yyzz = new ArrayList<>();
//        commitFileList(picCommit.getXkz(),xkz,dir,PicType.XKZ);
//        commitFileList(picCommit.getYyzz(),yyzz,dir,PicType.YYZZ);
//        PicPathRes picPathRes = new PicPathRes();
//        picPathRes.setXkzList(xkz);
//        picPathRes.setYyzzList(yyzz);
//        picCommitMap.remove(id);
//        return ApiResponse.success(picPathRes);
//
//    }

    public static void uploadFilesHelper(MultipartFile[] file, List<String> fileNameList, PicType picType, String dir,String number) throws IOException {

        File fileDir = new File(dir);
        boolean b = false;
        if (!fileDir.exists()) {
            b = fileDir.mkdirs();
        }
        //生成文件在服务器端存放的名字
        for (int i = 0; i < file.length; i++) {
            if (file[i].isEmpty()) {
                continue;
            }
            String filePath = uploadOneFile(file[i],dir,picType,number);
            fileNameList.add(filePath);
//            String fileSuffix = file[i].getOriginalFilename().substring(file[i].getOriginalFilename().lastIndexOf("."));
//            DateFormat df = new SimpleDateFormat("yyyyMMdd");
//            Calendar calendar = Calendar.getInstance();
//            String dateName = df.format(calendar.getTime());
//            String fileName = picType + "_" +number+"_"+dateName+"_"
//                    + UUID.randomUUID().toString().replace("-", "").substring(0,11) + fileSuffix;
//            String filePath = fileDir + "/" + fileName;
//            File files = new File(filePath);
//            log.info("文件名：{} 保存地址:{}", fileName, filePath);
//            fileNameList.add(filePath);
//            //上传
//            file[i].transferTo(files);
        }
    }

    private final static Integer FILE_SIZE = 50;//文件上传限制大小
    private final static String FILE_UNIT = "K";//文件上传限制单位（B,K,M,G）

    /**
     * @param len  文件长度
     * @param size 限制大小
     * @param unit 限制单位（B,K,M,G）
     * @描述 判断文件大小
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equalsIgnoreCase(unit)) {
            fileSize = (double) len;
        } else if ("K".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1024;
        } else if ("M".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1048576;
        } else if ("G".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1073741824;
        }
        return !(fileSize > size);
    }

    //文件上传调用
    public static ApiResponse checkFile(MultipartFile[] file) {
        boolean flag;
        if (file.length <= 0) {
            return ApiResponse.success();
        }
        for (MultipartFile multipartFile : file
        ) {
            flag = checkFileSize(multipartFile.getSize(), FILE_SIZE, FILE_UNIT);
            if (!flag) {
                return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR.getCode(), "上传文件大小超出限制");
            }
            flag = checkFileType(multipartFile.getOriginalFilename());
            if (!flag) {
                return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR.getCode(), "上传文件格式非法，只能上传:" + suffixList);
            }
        }
        return ApiResponse.success();
    }

    //文件上传调用
    public static ApiResponse checkOneFile(MultipartFile file) {
        boolean flag;
        if (file.isEmpty()) {
            return ApiResponse.success();
        }

        flag = checkFileSize(file.getSize(), FILE_SIZE, FILE_UNIT);
        if (!flag) {
            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR.getCode(), "上传文件大小超出限制");
        }
        flag = checkFileType(file.getOriginalFilename());
        if (!flag) {
            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR.getCode(), "上传文件格式非法，只能上传:" + suffixList);
        }

        return ApiResponse.success();
    }

    /**
     * 判断是否为允许的上传文件类型,true表示允许
     */
    private static final String suffixList = "jpg,png,jpeg";

    private static boolean checkFileType(String fileName) {
        //设置允许上传文件类型
//        String suffixList = "jpg,png,jpeg";
        // 获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".")
                + 1, fileName.length());
        if (suffixList.contains(suffix.trim().toLowerCase())) {
            return true;
        }
        return false;
    }

    private static void commitFileList(List<MultipartFile> list,List<String> fileNameList,String dir,PicType picType) throws IOException {
        if(!list.isEmpty()){
            //生成文件在服务器端存放的名字
            for (int i = 0; i < list.size(); i++) {
                MultipartFile f = list.get(i);
                String filePath = uploadOneFile(f,dir,picType,"");
                fileNameList.add(filePath);
            }
        }
    }

    /**
     * 上传单个文件，返回文件地址
     * @param f
     * @param dir
     * @return
     * @throws IOException
     */
    private static String uploadOneFile(MultipartFile f,String dir,PicType picType,String number) throws IOException {
        File fileDir = new File(dir);
        boolean b = false;
        if (!fileDir.exists()) {
            b = fileDir.mkdirs();
        }
        String filePath = Strings.EMPTY;
//        if(!f.isEmpty()){
            String fileSuffix = f.getOriginalFilename().substring(f.getOriginalFilename().lastIndexOf("."));
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            String dateName = df.format(calendar.getTime());
            String fileName = picType.getCode() + "_" +number+"_"+dateName+"_"
                    + UUID.randomUUID().toString().replace("-", "").substring(0,11) + fileSuffix;
            filePath = fileDir + "/" + fileName;
            File files = new File(filePath);
            log.info("文件名：{} 保存地址:{}", fileName, filePath);
            //上传
            f.transferTo(files);
//        }
        return filePath;

    }

    //file要与表单上传的名字相同
//    public static ApiResponse<List<String>> uploadFiles(MultipartFile[] file, String dir, String picType) {
//        try {
//            //创建文件在服务器端存放路径
////            String dir = request.getServletContext().getRealPath("/upload");
//            log.info("入参文件数：{}。开始上传文件到此路径：{}", file.length, dir);
//            if (file.length <= 0) {
//                return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(), ErrorCodeEnum.RESPONES_ERROR.getMessage() + "，文件数量为空");
//            }
//            File fileDir = new File(dir);
//            if (!fileDir.exists()) {
//                fileDir.mkdirs();
//            }
//            List<String> fileNameList = new ArrayList<>();
//            //生成文件在服务器端存放的名字
//            for (int i = 0; i < file.length; i++) {
//                String fileSuffix = file[i].getOriginalFilename().substring(file[i].getOriginalFilename().lastIndexOf("."));
//                String fileName = UUID.randomUUID().toString().replace("-", "") + fileSuffix;
//                String filePath = fileDir + "/" + fileName;
//                File files = new File(filePath);
//                log.info("文件地址:{}", filePath);
//                fileNameList.add(filePath);
//                //上传
//                file[i].transferTo(files);
//            }
//            return ApiResponse.success(fileNameList);
//        } catch (Exception e) {
////            e.printStackTrace();
//            log.error("", e);
//            return ApiResponse.fail("图片上传失败:{}", e.getMessage());
//        }
//    }

    /**
     * 上传单个文件，返回文件地址
     * @param f
     * @param dir
     * @return
     * @throws IOException
     */
    private static String uploadOneFileToSystem(MultipartFile f,String dir,PicType picType,String number) throws IOException {
        File fileDir = new File(dir);
        boolean b = false;
        if (!fileDir.exists()) {
            b = fileDir.mkdirs();
        }
        String filePath = Strings.EMPTY;
//        if(!f.isEmpty()){
        String fileSuffix = f.getOriginalFilename().substring(f.getOriginalFilename().lastIndexOf("."));
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        String dateName = df.format(calendar.getTime());
        String fileName = picType.getCode() + "_" +number+"_"+dateName+"_"
                + UUID.randomUUID().toString().replace("-", "").substring(0,11) + fileSuffix;
        filePath = fileDir + "/" + fileName;
        File files = new File(filePath);
        log.info("文件名：{} 保存地址:{}", fileName, filePath);
        //上传
        f.transferTo(files);
//        }
        return filePath;

    }

    public static String getBase64(String filePath){
        String imgStr = "";
        try {

            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length && (numRead = fis.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
            fis.close();
            BASE64Encoder encoder = new BASE64Encoder();
            imgStr = encoder.encode(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "data:image/jpeg;base64,"+imgStr;
    }

}
