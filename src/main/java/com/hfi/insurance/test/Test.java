//package com.hfi.insurance.test;
//
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.ModelAndView;
//
///**
// * @author jthealth-NZH
// * @Date 2021/9/15 11:43
// * @Describe
// * @Version 1.0
// */
//public class Test {
//    @RequestMapping(value = "/importfraud")
//    @ResponseBody
//    public ModelAndView importfraud(@RequestParam("excelPath") MultipartFile file, HttpServletRequest request) {
//        ModelAndView mav = new ModelAndView("/addfraud.html");
//        ImportFraudResponse importFraudResponse = new ImportFraudResponse();
//        ImportFraudRequest importFraudRequest = new ImportFraudRequest();
//        GetLoginUserResponse getLoginUserResponse = new GetLoginUserResponse();
//        try {
//            getLoginUserResponse = new Utils().getLoginUser(request);
//            if (getLoginUserResponse == null || getLoginUserResponse.getMisUser() == null) {
//                importFraudResponse.setResponseCode(2);
//                importFraudResponse.setErrorMessage("未取到用户登录信息！");
//                mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                return mav;
//            }
//        } catch (Exception e) {
//            importFraudResponse.setException(e);
//            importFraudResponse.setErrorMessage(e.getMessage());
//            importFraudResponse.setResponseCode(-1);
//            mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//            return mav;
//        }
//        MisUser misUser = getLoginUserResponse.getMisUser();
//        String loginName = misUser.getLoginName();
//        if (file == null) {
//            importFraudResponse.setErrorMessage("文件为空");
//            importFraudResponse.setResponseCode(2);
//            mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//            return mav;
//        }
//        long size = file.getSize();
//        // 获取上传文件的名字
//        String name = file.getOriginalFilename(); //判断文件格式
//        String nameSuffix = name.substring(name.lastIndexOf(".") + 1);
//        if (!nameSuffix.equals("xlsx")) {
//            importFraudResponse.setErrorMessage("非2007版Excel格式的文件不允许上传");
//            importFraudResponse.setResponseCode(2);
//            mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//            return mav;
//        }
//        if (null == name || "".equals(name) && size == 0) {
//            importFraudResponse.setErrorMessage("文件名为空");
//            importFraudResponse.setResponseCode(2);
//            mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//            return mav;
//        }
//        List<FraudDictBo> fraudDictBoList = new ArrayList<FraudDictBo>();
//        try { // 创建工作簿
//            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream()); // 得到工作表
//            XSSFSheet sheet = workbook.getSheetAt(0); // 对应excel的行
//            XSSFRow row = null; // 对应excel的列
//            XSSFCell cell = null; //
//            //得到excel的总记录数
//            int totalRow = sheet.getLastRowNum();
//            //限制Excel数据不超过500条
//            if (totalRow > 500) {
//                importFraudResponse.setErrorMessage("每次只能上传500条以内的数据！");
//                importFraudResponse.setResponseCode(2);
//                mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                return mav;
//            }
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            XSSFRow excelHead = sheet.getRow(0); //空表格
//            if (excelHead == null) {
//                importFraudResponse.setErrorMessage("没有表头！");
//                importFraudResponse.setResponseCode(2);
//                mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                return mav;
//            }
//            if (excelHead.getCell(0) != null && !"".equals(excelHead.getCell(0)) && excelHead.getCell(1) != null && !"".equals(excelHead.getCell(1)) && excelHead.getCell(2) != null && !"".equals(excelHead.getCell(2))) {
//                // 判断表头是否一一对应
//                if (!excelHead.getCell(0).toString().equals("fraudType") || !excelHead.getCell(1).toString().equals("fraudKey") || !excelHead.getCell(2).toString().equals("fraudValue")) {
//                    importFraudResponse.setErrorMessage("表格第一行表头数据不--对应");
//                    importFraudResponse.setResponseCode(2);
//                    mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                    return mav;
//                }
//            } else { //表头某列为空
//                importFraudResponse.setErrorMessage("表头有空列");
//                importFraudResponse.setResponseCode(2);
//                mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                return mav;
//            } //只有表头的空表格
//            if (totalRow < 1) {
//                importFraudResponse.setErrorMessage("表格的记录为空！");
//                importFraudResponse.setResponseCode(2);
//                mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                return mav;
//            } // errorList保存有特殊字符的行数
//            Set<Integer> errorSet = new HashSet<Integer>();
//            for (int i = 1; i <= totalRow; i++) {
//                row = sheet.getRow(i);
//                if (row == null) {
//                    importFraudResponse.setErrorMessage("第" + (i + 1) + "行为空，请修改后重新导入");
//                    importFraudResponse.setResponseCode(2);
//                    mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                    return mav;
//                }
//                FraudDictBo fraudDictBo = new FraudDictBo();
//                if (row.getCell(0) != null && !"".equals(row.getCell(0)) && row.getCell(1) != null && !"".equals(row.getCell(1)) && row.getCell(2) != null && !"".equals(row.getCell(2))) {
//                    cell = row.getCell(0);
//                    String fraudType = getCellValue(cell);
//                    if (!fraudType.equals("1") && !fraudType.equals("2")) {
//                        errorSet.add(i + 1);
//                        importFraudResponse.setErrorMessage("记录类型不正确");
//                        importFraudResponse.setResponseCode(2);
//                    }
//                    fraudDictBo.setFraudType(fraudType);
//                    cell = row.getCell(1);
//                    String fraudKey = getCellValue(cell);
//                    cell = row.getCell(2);
//                    String fraudValue = getCellValue(cell);
//                    fraudDictBo.setFraudKey(fraudKey);
//                    fraudDictBo.setFraudValue(fraudValue);
//                    if (fraudKey.equals("mobile")) { // 验证手机号
//                        if (checkValue(fraudValue) == false) {
//                            importFraudResponse.setErrorMessage("mobile值不正确");
//                            importFraudResponse.setResponseCode(2);
//                            errorSet.add(i + 1);
//                        }
//                    } else if (fraudKey.equals("bankcardno")) { // 验证银行卡号
//                        if (checkValue(fraudValue) == false) {
//                            importFraudResponse.setErrorMessage("bankcardno值不正确");
//                            importFraudResponse.setResponseCode(2);
//                            errorSet.add(i + 1);
//                        }
//                    } else if (fraudKey.equals("cardno")) { // 验证会员卡号
//                        if (checkValue(fraudValue) == false) {
//                            importFraudResponse.setErrorMessage("cardno值不正确");
//                            importFraudResponse.setResponseCode(2);
//                            errorSet.add(i + 1);
//                        }
//                    } else {
//                        errorSet.add(i + 1);
//                        importFraudResponse.setErrorMessage("黑名单记录项不正确");
//                        importFraudResponse.setResponseCode(2);
//                    }
//                    fraudDictBo.setReason("数据批量导入");
//                    fraudDictBo.setOperator(loginName);
//                    fraudDictBo.setOperateTime(sdf.format(new Date())); //添加之前判重
//                    int j = judgeRepeatFraud(fraudDictBo, fraudDictBoList);
//                    if (j > 0) { //提示重复的信息
//                        importFraudResponse.setErrorMessage("数据重复");
//                        importFraudResponse.setRepeatMsg("您导入的数据第" + (i + 1) + "行和第" + j + "行重复！");
//                        importFraudResponse.setResponseCode(3);
//                        mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                        return mav;
//                    }
//                    fraudDictBoList.add(fraudDictBo);
//                } else { //判断是否含有空列
//                    importFraudResponse.setErrorMessage("第" + (i + 1) + "行有空列");
//                    importFraudResponse.setResponseCode(2);
//                    mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                    return mav;
//                }
//            } //提示有非法值
//            if (errorSet.size() > 0) {
//                importFraudResponse.setErrorMessage(printErrorMsg(errorSet));
//                importFraudResponse.setResponseCode(2);
//                mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                return mav;
//            }
//        } catch (FileNotFoundException e1) {
//            importFraudResponse.setException(e1);
//            importFraudResponse.setErrorMessage("找不到文件");
//            importFraudResponse.setResponseCode(-1);
//            mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//            return mav;
//        } catch (IOException e) {
//            importFraudResponse.setException(e);
//            importFraudResponse.setErrorMessage("读文件异常");
//            importFraudResponse.setResponseCode(-1);
//            mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//            return mav;
//        }
//        String url = this.getServiceApiUrl("frauddict", "importfraud"); //
//        String url = "http://127.0.0.1:8080/frauddict/importfraud";
//        String result = ""; // 每次要发送的记录数
//        int sendSize = Integer.parseInt(ConfigurationManager.appSettings("sendSize", "conf/custom/notenv/importFraud")); // 要发送的list总记录数
//        int totalRecord = fraudDictBoList.size(); // 循环的次数
//        int cycle = totalRecord / sendSize;
//        List<FraudDictBo> tempFraudDictBoList = new ArrayList<FraudDictBo>();
//        if (totalRecord % sendSize != 0) {
//            cycle += 1;
//            if (totalRecord < sendSize) {
//                sendSize = totalRecord;
//            }
//        }
//        for (int i = 0; i < cycle; i++) {
//            if (fraudDictBoList.size() < sendSize) {
//                sendSize = fraudDictBoList.size();
//            }
//            for (int j = 0; j < sendSize; j++) {
//                tempFraudDictBoList.add(fraudDictBoList.get(j));
//            }
//            importFraudRequest.setFraudDictBoList(tempFraudDictBoList);
//            try {
//                HttpHelper helper = HttpHelper.connect(url).timeout(Integer.parseInt(ConfigurationManager.appSettings("importFraudTimeOut", "conf/custom/notenv/importFraud")));
//                helper.data("json", JsonHelper.toJson(importFraudRequest));
//                result = helper.post().html();
//            } catch (Exception e) {
//                importFraudResponse.setException(e);
//                importFraudResponse.setErrorMessage(e.getMessage());
//                importFraudResponse.setResponseCode(-1);
//                mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//                return mav;
//            } // 清除已经保存的数据
//            fraudDictBoList.removeAll(tempFraudDictBoList);
//            tempFraudDictBoList.clear();
//        }
//        importFraudResponse = JsonHelper.fromJson(result, ImportFraudResponse.class);
//        mav.addObject("importFraudResponse", JSON.toJSONString(importFraudResponse));
//        return mav;
//    }
//
//}
