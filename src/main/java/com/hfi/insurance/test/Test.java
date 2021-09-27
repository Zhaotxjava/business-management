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
//        JSONObject jsonObject1 = JSONObject.parseObject("{\"xkzList\":[\"$4530468d-d9b0-49d2-80d5-78fc55e680cb$2770357584\",\"$4530468d-d9b0-49d2-80d5-78fc55e680cb$2770357584\"],\"yyzzList\":[]}");
//        JSONArray jsonArray = jsonObject1.getJSONArray("xkzList");
//        for (int i = 0; i < jsonArray.size(); i++) {
//            jsonArray.get(i);
//        }

//        JSONArray jsonArray = JSONObject.parseArray("[\"E:\\\\Temp\\\\img/xkz_872042ad4f3d4a2b95dd3c1975fc3276.jpg\",\"E:\\\\Temp\\\\img/xkz_6169fa35861d48aeb79645141e4dec80.png\"]");
//        System.out.println(JSONObject.toJSONString(jsonArray));
//        ExcelReader reader = ExcelUtil.getReader("E:\\Temp\\source\\"+"新建XLS01 工作表.xls");
//        List<List<Object>> readAll = reader.read();
//        System.out.println(readAll.toString());
        Set<String> set = new HashSet<>();
        String s ="aa";
        set.add("aa");
        set.add("aa");
        set.add(s);
        s = "b";
        set.add(s);
        System.out.println(set.toString());

    }


}
