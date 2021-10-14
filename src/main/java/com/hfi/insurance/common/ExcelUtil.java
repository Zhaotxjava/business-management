package com.hfi.insurance.common;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import com.hfi.insurance.config.ExportExcel;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelUtil {

    //导出excel的工具类
    public static void exportExcel(List<?> data, HttpServletResponse response) {
        try {
            XSSFWorkbook excel = new XSSFWorkbook();//创建excel
            XSSFSheet sheet = excel.createSheet();
            XSSFRow title = sheet.createRow(0);
            //要用反射,必须先拿到Class对象
            Class<?> cls = data.get(0).getClass();
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {//循环所有字段
                ExportExcel annotation = fields[i].getAnnotation(ExportExcel.class);//获取字段上面的ExportExcel注解
                if (annotation != null) {//获取我们自定义注解,如果不为空,则标识字段上有这个注解
                    title.createCell(i).setCellValue(annotation.name());
                    sheet.setColumnWidth(i, 30 * 256);
                }
            }
            for (int i = 0; i < data.size(); i++) {//循环集合,创建row行
                XSSFRow row = sheet.createRow(i + 1);//创建一行
                Object obj = data.get(i);//获取对象
                for (int j = 0; j < fields.length; j++) {//循环所有字段
                    ExportExcel annotation = fields[j].getAnnotation(ExportExcel.class);//获取字段上面的ExportExcel注解
                    if (annotation != null) {//如果不等于空,则通过反射,获取字段中的数据

                        fields[j].setAccessible(true);//让他有权限获取私有字段
                        if (fields[j].get(obj) != null) {
                            row.createCell(j).setCellValue(fields[j].get(obj).toString());
                        }
                    }
                }
            }
            response.reset(); // 重点突出
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-msdownload");
            // 告诉浏览器返回的文件的名称
            response.setHeader("Content-Disposition", "attachment;filename=" + UUID.randomUUID().toString() + ".xlsx");// 重点突出
            excel.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void xlsDownloadFile(HttpServletResponse response, Workbook wb) {
        OutputStream os = null;
        try {
            os = response.getOutputStream(); //重点突出(特别注意),通过response获取的输出流，作为服务端向客户端浏览器输出内容的通道
            // 处理下载文件名的乱码问题(根据浏览器的不同进行处理)
            response.reset(); // 重点突出
            response.setCharacterEncoding("UTF-8"); // 重点突出
            response.setContentType("application/x-msdownload");// 不同类型的文件对应不同的MIME类型 // 重点突出
            response.setHeader("Content-Disposition", "attachment;filename=" + UUID.randomUUID().toString() + ".xlsx");// 重点突出
            wb.write(os);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            // 特别重要
            // 1. 进行关闭是为了释放资源
            // 2. 进行关闭会自动执行flush方法清空缓冲区内容
            try {
                if (null != os) {
                    os.close();
                    os = null;
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    public static void xlsDownloadFile2(HttpServletResponse response, Workbook wb, String fileName) {
        OutputStream os = null;
        try {
            os = response.getOutputStream(); //重点突出(特别注意),通过response获取的输出流，作为服务端向客户端浏览器输出内容的通道
            // 处理下载文件名的乱码问题(根据浏览器的不同进行处理)
            response.reset(); // 重点突出
            response.setCharacterEncoding("ISO-8859-1"); // 重点突出
            response.setContentType("application/x-msdownload");// 不同类型的文件对应不同的MIME类型 // 重点突出
            response.setHeader("Content-Disposition", "attachment;filename=" + UUID.randomUUID().toString()+ ".xlsx");// 重点突出
            wb.write(os);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            // 特别重要
            // 1. 进行关闭是为了释放资源
            // 2. 进行关闭会自动执行flush方法清空缓冲区内容
            try {
                if (null != os) {
                    os.close();
                    os = null;
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    public static void exportExcel2(List<?> data, XSSFWorkbook excel, String name) {
        try {
            XSSFSheet sheet = excel.createSheet(name);
            XSSFRow title = sheet.createRow(0);
            //要用反射,必须先拿到Class对象
            Class<?> cls = data.get(0).getClass();
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {//循环所有字段
                ExportExcel annotation = fields[i].getAnnotation(ExportExcel.class);//获取字段上面的ExportExcel注解
                if (annotation != null) {//获取我们自定义注解,如果不为空,则标识字段上有这个注解
                    title.createCell(i).setCellValue(annotation.name());
                    sheet.setColumnWidth(i, 30 * 256);
                }
            }

            for (int i = 0; i < data.size(); i++) {//循环集合,创建row行
                XSSFRow row = sheet.createRow(i + 1);//创建一行
                Object obj = data.get(i);//获取对象
                for (int j = 0; j < fields.length; j++) {//循环所有字段
                    ExportExcel annotation = fields[j].getAnnotation(ExportExcel.class);//获取字段上面的ExportExcel注解
                    if (annotation != null) {//如果不等于空,则通过反射,获取字段中的数据

                        fields[j].setAccessible(true);//让他有权限获取私有字段
                        if (fields[j].get(obj) != null) {
                            row.createCell(j).setCellValue(fields[j].get(obj).toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
