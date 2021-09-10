package com.hfi.insurance.utils;

import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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
    //file要与表单上传的名字相同
    public static ApiResponse<List<String>> uploadFiles(MultipartFile[] file,String dir ,String picType) {
        try {
            //创建文件在服务器端存放路径
//            String dir = request.getServletContext().getRealPath("/upload");
            log.info("入参文件数：{}。开始上传文件到此路径：{}",file.length,dir);
            if(file.length<=0){
                return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(),ErrorCodeEnum.RESPONES_ERROR.getMessage()+"，文件数量为空");
            }
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            List<String> fileNameList = new ArrayList<>();
            //生成文件在服务器端存放的名字
            for (int i = 0; i < file.length; i++) {
                String fileSuffix = file[i].getOriginalFilename().substring(file[i].getOriginalFilename().lastIndexOf("."));
                String fileName = picType+"_"+UUID.randomUUID().toString().replace("-","") + fileSuffix;
                String filePath = fileDir + "/" + fileName;
                File files = new File(filePath);
                log.info("文件地址:{}",filePath);
                fileNameList.add(filePath);
                //上传
                file[i].transferTo(files);
            }
            return ApiResponse.success(fileNameList);
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("",e);
            return ApiResponse.fail("图片上传失败:{}",e.getMessage());
        }
    }
}
