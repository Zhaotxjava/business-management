//package com.hfi.insurance.service.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.hfi.insurance.common.ApiResponse;
//import com.hfi.insurance.enums.ErrorCodeEnum;
//import com.hfi.insurance.enums.PicType;
//import com.hfi.insurance.model.InstitutionInfo;
//import com.hfi.insurance.model.dto.PicCommitPath;
//import com.hfi.insurance.utils.PicUploadUtil;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Objects;
//
///**
// * @author jthealth-NZH
// * @Date 2021/9/17 18:53
// * @Describe
// * @Version 1.0
// */
//@Service
//public class PicUploadService {
//
//
////    public static ApiResponse cacheFile(MultipartFile file, String operationId, String picType, String dir, String bumber) throws IOException {
////        ApiResponse apiResponse = PicUploadUtil.checkOneFile(file);
////        if(!apiResponse.isSuccess()){
////            return apiResponse;
////        }
////        PicCommitPath picCommit = PicUploadUtil.picCommitPath.get(operationId);
////        if (Objects.isNull(picCommit)) {
////            picCommit = new PicCommitPath();
////        }
////        PicType type = PicType.getPicType(picType);
////        String path = uploadOneFile(file,dir,type,bumber);
//////        String path = uploadOneFile();
////        switch (type){
////            case XKZ:
////                picCommit.getXkz().add(path);
////                break;
////            case YYZZ:
////                picCommit.getYyzz().add(path);
////                break;
////            default:
////                return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR,"图片类型非法");
////        }
////        picCommitPath.put(operationId,picCommit);
////        return ApiResponse.success();
////    }
//
//}
