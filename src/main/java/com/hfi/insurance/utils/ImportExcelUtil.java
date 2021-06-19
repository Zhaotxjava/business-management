
package com.hfi.insurance.utils;

import com.hfi.insurance.enums.ExcelVersion;
import com.hfi.insurance.model.ExcelSheetPO;
//import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/3/10 4:43
 * @Description:
 */
public class ImportExcelUtil {
    /**
     * 标题样式
     */
    private static final String STYLE_HEADER = "header";
    /**
     * 表头样式
     */
    private static final String STYLE_TITLE = "title";
    /**
     * 数据样式
     */
    private static final String STYLE_DATA = "data";

    /**
     * 存储样式
     */
    private static final HashMap<String, CellStyle> cellStyleMap = new HashMap<>();

    /**
     * 读取excel文件里面的内容 支持日期，数字，字符，函数公式，布尔类型
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException           , Integer rowCount, Integer columnCount
     * @author JIANGYOUYAO
     * @email 935090232@qq.com
     * @date 2017年12月20日
     */
    public static List<ExcelSheetPO> readExcel(MultipartFile file)
            throws FileNotFoundException, IOException {

        // 根据后缀名称判断excel的版本
        String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        Workbook wb;
        if (ExcelVersion.V2003.getSuffix().equals(extName)) {
            wb = new HSSFWorkbook(file.getInputStream());

        } else if (ExcelVersion.V2007.getSuffix().equals(extName)) {
            wb = new XSSFWorkbook(file.getInputStream());

        } else {
            // 无效后缀名称，这里之能保证excel的后缀名称，不能保证文件类型正确，不过没关系，在创建Workbook的时候会校验文件格式
            throw new IllegalArgumentException("Invalid excel version");
        }
        // 开始读取数据
        List<ExcelSheetPO> sheetPOs = new ArrayList<>();
        // 解析sheet
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            List<List<Object>> dataList = new ArrayList<>();
            ExcelSheetPO sheetPO = new ExcelSheetPO();
            sheetPO.setSheetName(sheet.getSheetName());
            sheetPO.setDataList(dataList);
            int readRowCount = sheet.getPhysicalNumberOfRows();
            // 解析sheet 的行
            for (int j = sheet.getFirstRowNum(); j < readRowCount; j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                if (row.getFirstCellNum() < 0) {
                    continue;
                }
                int readColumnCount = (int) row.getLastCellNum();
                List<Object> rowValue = new LinkedList<Object>();
                // 解析sheet 的列
                for (int k = 0; k < readColumnCount; k++) {

                    //判断是否是合并单元格
                    boolean isMerge = isMergedRegion(sheet, j, k);
                    if (isMerge) {
                        //获取合并单元格的值
                        String mergedRegionValue = getMergedRegionValue(sheet, j, k, wb);
                        rowValue.add(mergedRegionValue+ StringUtils.EMPTY);
                    } else {
                        Cell cell = row.getCell(k);
                        rowValue.add(getCellValue(wb, cell));
                    }

                }
                dataList.add(rowValue);
            }
            sheetPOs.add(sheetPO);
        }
        return sheetPOs;
    }


    public static List<ExcelSheetPO> readExcel(InputStream inputStream, ExcelVersion excelVersion)
            throws IOException {

        // 根据后缀名称判断excel的版本
        Workbook wb;
        if (ExcelVersion.V2003.equals(excelVersion)) {
            wb = new HSSFWorkbook(inputStream);

        } else if (ExcelVersion.V2007.equals(excelVersion)) {
            wb = new XSSFWorkbook(inputStream);

        } else {
            // 无效后缀名称，这里之能保证excel的后缀名称，不能保证文件类型正确，不过没关系，在创建Workbook的时候会校验文件格式
            throw new IllegalArgumentException("Invalid excel version");
        }
        // 开始读取数据
        List<ExcelSheetPO> sheetPOs = new ArrayList<>();
        // 解析sheet
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            List<List<Object>> dataList = new ArrayList<>();
            ExcelSheetPO sheetPO = new ExcelSheetPO();
            sheetPO.setSheetName(sheet.getSheetName());
            sheetPO.setDataList(dataList);
            int readRowCount = sheet.getPhysicalNumberOfRows();
            // 解析sheet 的行
            for (int j = sheet.getFirstRowNum(); j < readRowCount; j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                if (row.getFirstCellNum() < 0) {
                    continue;
                }
                int readColumnCount = row.getLastCellNum();
                List<Object> rowValue = new LinkedList<>();
                // 解析sheet 的列
                for (int k = 0; k < readColumnCount; k++) {

                    //判断是否是合并单元格
                    boolean isMerge = isMergedRegion(sheet, j, k);
                    if (isMerge) {
                        //获取合并单元格的值
                        String mergedRegionValue = getMergedRegionValue(sheet, j, k, wb);
                        rowValue.add(mergedRegionValue);
                    } else {
                        Cell cell = row.getCell(k);
                        rowValue.add(getCellValue(wb, cell));
                    }

                }
                dataList.add(rowValue);
            }
            sheetPOs.add(sheetPO);
        }
        return sheetPOs;
    }


    public static Object getCellValue(Workbook wb, Cell cell) {
        Object columnValue = null;
        if (cell != null) {
            // String
            // 字符
            // 格式化日期字符串
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    columnValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    //使用DecimalFormat类对科学计数法格式的数字进行格式化
                        DecimalFormat df = new DecimalFormat("0");
                        columnValue = df.format(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    columnValue = cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                    columnValue = "";
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    // 格式单元格
                    FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
                    evaluator.evaluateFormulaCell(cell);
                    CellValue cellValue = evaluator.evaluate(cell);
                    columnValue = cellValue.getNumberValue();
                    break;
                default:
                    columnValue = cell.toString();
            }
        }
        return columnValue;
    }

    /**
     * 在硬盘上写入excel文件
     *
     * @param version
     * @param excelSheets
     * @param filePath
     * @throws IOException
     * @author JIANGYOUYAO
     * @email 935090232@qq.com
     * @date 2017年12月20日
     */
    public static void createWorkbookAtDisk(ExcelVersion version, List<ExcelSheetPO> excelSheets, String filePath)
            throws IOException {
        File file = new File(filePath,"医保定点机构列表20210607.xlsx");
        if (file.exists()){
            file.delete();
        }
        FileOutputStream fileOut = new FileOutputStream(file);
        createWorkbookAtOutStream(version, excelSheets, fileOut, true);
    }


    /**
     * 把excel表格写入输出流中，输出流会被关闭
     *
     * @param version
     * @param excelSheets
     * @param outStream
     * @param closeStream 是否关闭输出流
     * @throws IOException
     * @author JIANGYOUYAO
     * @email 935090232@qq.com
     * @date 2017年12月20日
     */
    public static void createWorkbookAtOutStream(ExcelVersion version, List<ExcelSheetPO> excelSheets,
                                                 OutputStream outStream, boolean closeStream) throws IOException {
        if (CollectionUtils.isNotEmpty(excelSheets)) {
            Workbook wb = createWorkBook(version, excelSheets);
            wb.write(outStream);
            if (closeStream) {
                outStream.close();
            }
        }
    }

    private static Workbook createWorkBook(ExcelVersion version, List<ExcelSheetPO> excelSheets) {
        Workbook wb = createWorkbook(version);
        for (int i = 0; i < excelSheets.size(); i++) {
            ExcelSheetPO excelSheetPO = excelSheets.get(i);
            if (excelSheetPO.getSheetName() == null) {
                excelSheetPO.setSheetName("sheet" + i);
            }
            // 过滤特殊字符
            Sheet tempSheet = wb.createSheet(WorkbookUtil.createSafeSheetName(excelSheetPO.getSheetName()));
            buildSheetData(wb, tempSheet, excelSheetPO, version);
        }
        return wb;
    }

    private static void buildSheetData(Workbook wb, Sheet sheet, ExcelSheetPO excelSheetPO, ExcelVersion version) {
        sheet.setDefaultRowHeight((short) 400);
        sheet.setDefaultColumnWidth((short) 10);
        //createTitle(sheet, excelSheetPO, wb, version);
        createHeader(sheet, excelSheetPO, wb, version);
        createBody(sheet, excelSheetPO, wb, version);
    }

    private static void createBody(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        List<List<Object>> dataList = excelSheetPO.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            List<Object> values = dataList.get(i);
            Row row = sheet.createRow(2 + i);
            for (int j = 0; j < values.size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(getStyle(STYLE_DATA, wb));
                if (values.get(j) != null){
                    cell.setCellValue(values.get(j).toString());
                }
            }
        }

    }

    private static void createHeader(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        String[] headers = excelSheetPO.getHeaders();
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length && i < version.getMaxColumn(); i++) {
            Cell cellHeader = row.createCell(i);
            cellHeader.getCellStyle().cloneStyleFrom(getStyle(STYLE_HEADER, wb));
//            cellHeader.setCellStyle(getStyle(STYLE_HEADER, wb));
            cellHeader.setCellValue(headers[i]);
        }

    }

    private static void createTitle(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        Row titleRow = sheet.createRow(0);
        Cell titleCel = titleRow.createCell(0);
        titleCel.setCellValue(excelSheetPO.getTitle());
        titleCel.setCellStyle(getStyle(STYLE_TITLE, wb));
        // 限制最大列数
        int column = excelSheetPO.getDataList().size() > version.getMaxColumn() ? version.getMaxColumn()
                : excelSheetPO.getDataList().size();
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, column - 1));
    }

    private static CellStyle getStyle(String type, Workbook wb) {

        if (cellStyleMap.containsKey(type)) {
            return cellStyleMap.get(type);
        }
        // 生成一个样式
        CellStyle style = wb.createCellStyle();
        style.setWrapText(true);

        if (STYLE_HEADER.equals(type)) {
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 16);
            style.setFont(font);
        } else if (STYLE_TITLE.equals(type)) {
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 18);
            style.setFont(font);
        } else if (STYLE_DATA.equals(type)) {
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            style.setFont(font);
        }
        cellStyleMap.put(type, style);
        return style;
    }

    public static Workbook createWorkbook(ExcelVersion version) {
        switch (version) {
            case V2003:
                return new HSSFWorkbook();
            case V2007:
                return new XSSFWorkbook();
            default:
                return null;
        }
    }


    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取合并单元格的值
     */
    private static String getMergedRegionValue(Sheet sheet, int row, int column, Workbook wb) {
        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow) {

                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(wb, fCell).toString();
                }
            }
        }
        return null;
    }

    /**
     * 获取不标红的所有cell的data值
     */
    public static List<ExcelSheetPO> readExcelWithOutRedFont(MultipartFile file)
            throws FileNotFoundException, IOException {
        // 根据后缀名称判断excel的版本
        String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        Workbook wb = null;
        if (ExcelVersion.V2003.getSuffix().equals(extName)) {
            wb = new HSSFWorkbook(file.getInputStream());

        } else if (ExcelVersion.V2007.getSuffix().equals(extName)) {
            wb = new XSSFWorkbook(file.getInputStream());

        } else {
            // 无效后缀名称，这里之能保证excel的后缀名称，不能保证文件类型正确，不过没关系，在创建Workbook的时候会校验文件格式
            throw new IllegalArgumentException("Invalid excel version");
        }
        // 开始读取数据
        List<ExcelSheetPO> sheetPOs = new ArrayList<>();
        // 解析sheet
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            List<List<Object>> dataList = new ArrayList<>();
            ExcelSheetPO sheetPO = new ExcelSheetPO();
            sheetPO.setSheetName(sheet.getSheetName());
            sheetPO.setDataList(dataList);
            int readRowCount = sheet.getPhysicalNumberOfRows();
            // 解析sheet 的行
            for (int j = sheet.getFirstRowNum(); j < readRowCount; j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                if (row.getFirstCellNum() < 0) {
                    continue;
                }
                int readColumnCount = (int) row.getLastCellNum();
                List<Object> rowValue = new LinkedList<Object>();
                boolean isRed = false;
                // 解析sheet 的列
                for (int k = 0; k < readColumnCount; k++) {
                    Cell cell = row.getCell(k);
                    System.out.println(((XSSFCell)cell).getCellStyle().getFont().getColor());
                    if(((XSSFCell)cell).getCellStyle().getFont().getColor()== Font.COLOR_RED){
                        isRed=true;
                    }
                    else if(((XSSFCell)cell).getCellStyle().getFont().getColor()==Font.COLOR_RED){
                        isRed=true;
                    }
                    //判断是否是合并单元格
                    boolean isMerge = isMergedRegion(sheet, j, k);
                    if (isMerge) {
                        //获取合并单元格的值
                        String mergedRegionValue = getMergedRegionValue(sheet, j, k, wb);
                        rowValue.add(mergedRegionValue);
                    } else {

                        rowValue.add(getCellValue(wb, cell));
                    }

                }
                if(!isRed){
                    dataList.add(rowValue);
                }

            }
            sheetPOs.add(sheetPO);
        }
        return sheetPOs;
    }

    /**
     * 获取合并单元格的第一个小单元格
     */
    public static Cell getMergedRegionFirstCell(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow) {

                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    return fRow.getCell(firstColumn);
                }
            }
        }
        return null;
    }
}
