package com.hfi.insurance.test;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jthealth-NZH
 * @Date 2021/9/15 11:43
 * @Describe
 * @Version 1.0
 */
public class Test {
    public static void main(String[] args) {
        JSONObject jsonObject1 = JSONObject.parseObject("{\"xkzList\":[\"/opt/app/insurance-info-server/pic/xkz_0150_20210916_947150eda8f.png\"],\"yyzzList\":[]}");
        System.out.println(String.valueOf(jsonObject1.get("xkzList")));
    }


}
