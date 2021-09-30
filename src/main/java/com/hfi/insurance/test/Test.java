package com.hfi.insurance.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jthealth-NZH
 * @Date 2021/9/15 11:43
 * @Describe
 * @Version 1.0
 */
public class Test {
    public static void main(String[] args) {


        Integer[] ss = {null, 1, 4, null, 8, 9, 3};

        for (Integer i : ss) {
            if (i == null) {

                continue;

            }

            switch (i) {
                case 4:
                    System.out.println(i);
                    break;
                case 9:
                    System.out.println(i);
                    break;
            }
        }
    }
}
