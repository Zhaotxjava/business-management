package com.hfi.insurance.utils;

import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.enums.PicType;
import com.hfi.insurance.model.PicPathRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author jthealth-NZH
 * @Date 2021/9/8 15:23
 * @Describe
 * @Version 1.0
 */
@Slf4j
public class PicUploadUtil {

    public static ApiResponse<PicPathRes> uploadFiles2(MultipartFile[] xxk, MultipartFile[] yyzz, String dir) {
        try {
            //创建文件在服务器端存放路径
//            String dir = request.getServletContext().getRealPath("/upload");
            log.info("入参文件数xxk：{}。yyzz：{}。开始上传文件到此路径：{}", xxk.length, yyzz.length, dir);
            if (xxk.length <= 0 && yyzz.length <= 0) {
                return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(), ErrorCodeEnum.RESPONES_ERROR.getMessage() + "，文件数量为空");
            }
            List<String> xxkList = new ArrayList<>();
            List<String> yyzzList = new ArrayList<>();
            uploadFilesHelper(xxk, xxkList, dir, PicType.XKZ.getCode());
            uploadFilesHelper(yyzz, yyzzList, dir, PicType.YYZZ.getCode());
            PicPathRes res = new PicPathRes();
            res.setXxkList(xxkList);
            res.setYyzzList(yyzzList);
            return ApiResponse.success(res);
        } catch (Exception e) {
            log.error("", e);
            return ApiResponse.fail("图片上传失败:{}", e.getMessage());
        }
    }

    public static void uploadFilesHelper(MultipartFile[] file, List<String> fileNameList, String picType, String dir) throws IOException {
        File fileDir = new File(dir);
        boolean b = false;
        if (!fileDir.exists()) {
            b = fileDir.mkdirs();
        }
        if(!b){
            throw new RuntimeException("创建文件夹失败");
        }
        //生成文件在服务器端存放的名字
        for (int i = 0; i < file.length; i++) {
            String fileSuffix = file[i].getOriginalFilename().substring(file[i].getOriginalFilename().lastIndexOf("."));
            String fileName = picType + "_" + UUID.randomUUID().toString().replace("-", "") + fileSuffix;
            String filePath = fileDir + "/" + fileName;
            File files = new File(filePath);
            log.info("文件名：{} 保存地址:{}", fileName, filePath);
            fileNameList.add(filePath);
            //上传
            file[i].transferTo(files);
        }
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
}
