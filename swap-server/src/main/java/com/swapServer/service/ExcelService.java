package com.swapServer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ExcelService {

    /**
     * <p>构建流式excel</p>
     * <p>使用默认流式窗口大小：100，SXSSFWorkbook当超过窗口大小的行数时进行 flush disk，清理内存</p>
     *
     * @param data  excel中数据集合
     * @param clazz 数据的类型
     * @param <T>   数据类型，此类型中的字段需要标记{@link ExcelLabeled}注解，不支持父类的字段
     * @return SXSSFWorkbook，注意：此对象需要调用{@link SXSSFWorkbook#dispose()} 方法进行清理临时文件
     * @see ExcelLabeled
     * @see #buildStreamWorkbook(List, Class, int)
     */
    public <T> SXSSFWorkbook buildStreamWorkbook(List<T> data, Class<T> clazz) {
        return buildStreamWorkbook(data, clazz, SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
    }

    /**
     * 构建流式excel
     *
     * @param data       excel中数据集合
     * @param clazz      数据的类型
     * @param windowSize 流式窗口大小，SXSSFWorkbook当超过窗口大小的行数时进行 flush disk，清理内存
     * @param <T>        数据类型，此类型中的字段需要标记{@link ExcelLabeled}注解，不支持父类的字段
     * @return SXSSFWorkbook，注意：此对象需要调用{@link SXSSFWorkbook#dispose()} 方法进行清理临时文件
     * @see ExcelLabeled
     */
    public <T> SXSSFWorkbook buildStreamWorkbook(List<T> data, Class<T> clazz, int windowSize) {
        //收集待处理的数据类型中的被注解标注的字段
        Field[] fields = clazz.getDeclaredFields();
        List<Field> labeledFields = Stream.of(fields).filter(field -> field.isAnnotationPresent(ExcelLabeled.class))
                .sorted(Comparator.comparing(field -> field.getAnnotation(ExcelLabeled.class).order()))
                .collect(Collectors.toList());
        if (labeledFields.size() == 0) {
            log.error("No fields labeled by @ExcelLabeled annotation.");
            throw new IllegalArgumentException("No fields labeled by @ExcelLabeled annotation.");
        }

        SXSSFWorkbook workbook = new SXSSFWorkbook(windowSize);
        Sheet sheet = workbook.createSheet();
        Row headRow = sheet.createRow(0);
        for (int i = 0; i < labeledFields.size(); i++) {
            Field field = labeledFields.get(i);
            headRow.createCell(i).setCellValue(field.getAnnotation(ExcelLabeled.class).value());
        }
        for (int i = 0; i < data.size(); i++) {
            T line = data.get(i);
            Row lineRow = sheet.createRow(i + 1);
            for (int j = 0; j < labeledFields.size(); j++) {
                Field field = labeledFields.get(j);
                field.setAccessible(true);
                ExcelLabeled excelLabeled = field.getAnnotation(ExcelLabeled.class);
                switch (excelLabeled.slot()) {
                    case NONE:
                        try {
                            Object value = field.get(line);
                            String valueStr;
                            if (value instanceof Instant) {
                                log.debug("OffSet:[{}]", OffsetDateTime.now().getOffset().getId());
                                valueStr = LocalDateTime.from(((Instant) value).atOffset(OffsetDateTime.now().getOffset()))
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                log.debug("Instant to local date time:[from:{},to:{}]", value.toString(), valueStr);
                            } else {
                                valueStr = value == null ? "" : value.toString();
                            }

                            lineRow.createCell(j).setCellValue(valueStr == null ? "" : valueStr);//default all string
                        } catch (IllegalAccessException e) {
                            log.error("Can't get the field [{}] value", field.getName());
                            throw new IllegalArgumentException("Can't get the field value.");
                        }
                        break;
                    case FILED:
                        String slotValue = excelLabeled.slotValue();
                        try {
                            Object value = field.get(line);
                            String slotFieldValue = "";
                            if (value != null) {
                                Field slotField = value.getClass().getDeclaredField(slotValue);
                                slotField.setAccessible(true);
                                slotFieldValue = slotField.get(value) == null ? "" : slotField.get(value).toString();
                            }
                            lineRow.createCell(j).setCellValue(slotFieldValue);
                        } catch (Exception e) {
                            log.error("Can't get the field [{}] value", field.getName());
                            throw new IllegalArgumentException("Can't get the field value.");
                        }
                        break;
                    case METHOD:
                        String slotValue2 = excelLabeled.slotValue();
                        try {
                            Object value = field.get(line);
                            String slotMethodValue = "";
                            if (value != null) {
                                Method slotMethod = value.getClass().getMethod(slotValue2);
                                slotMethod.setAccessible(true);
                                Object result = slotMethod.invoke(value);
                                slotMethodValue = result == null ? "" : result.toString();
                            }
                            lineRow.createCell(j).setCellValue(slotMethodValue);
                        } catch (Exception e) {
                            log.error("Can't get the field [{}] value", field.getName());
                            throw new IllegalArgumentException("Can't get the field value.");
                        }
                        break;
                }
            }
        }
        return workbook;
    }

    public <T> String buildStreamCsvText(List<T> data, Class<T> clazz) {
        StringBuilder longText = new StringBuilder("");
        //收集待处理的数据类型中的被注解标注的字段
        Field[] fields = clazz.getDeclaredFields();
        List<Field> labeledFields = Stream.of(fields).filter(field -> field.isAnnotationPresent(ExcelLabeled.class))
                .sorted(Comparator.comparing(field -> field.getAnnotation(ExcelLabeled.class).order()))
                .collect(Collectors.toList());
        if (labeledFields.size() == 0) {
            log.error("No fields labeled by @ExcelLabeled annotation.");
            throw new IllegalArgumentException("No fields labeled by @ExcelLabeled annotation.");
        }
        List<String> collect = labeledFields.stream().map(x -> x.getAnnotation(ExcelLabeled.class).value()).collect(Collectors.toList());
        longText.append(collect.toString().replace("[", "").replace("]", "").trim());
        longText.append("\n");
        for (int i = 0; i < data.size(); i++) {
            T line = data.get(i);
            StringBuilder lineSb = new StringBuilder("");
            for (int j = 0; j < labeledFields.size(); j++) {
                Field field = labeledFields.get(j);
                field.setAccessible(true);
                ExcelLabeled excelLabeled = field.getAnnotation(ExcelLabeled.class);
                switch (excelLabeled.slot()) {
                    case NONE:
                        try {
                            Object value = field.get(line);
                            String valueStr;
                            if (value instanceof Instant) {
                                log.debug("OffSet:[{}]", OffsetDateTime.now().getOffset().getId());
                                valueStr = LocalDateTime.from(((Instant) value).atOffset(OffsetDateTime.now().getOffset()))
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                log.debug("Instant to local date time:[from:{},to:{}]", value.toString(), valueStr);
                            } else {
                                valueStr = value == null ? "" : value.toString();
                            }
                            lineSb.append(valueStr == null ? "," : valueStr + ",");//default all string
                        } catch (IllegalAccessException e) {
                            log.error("Can't get the field [{}] value", field.getName());
                            throw new IllegalArgumentException("Can't get the field value.");
                        }
                        break;
                    case FILED:
                        String slotValue = excelLabeled.slotValue();
                        try {
                            Object value = field.get(line);
                            String slotFieldValue = "";
                            if (value != null) {
                                Field slotField = value.getClass().getDeclaredField(slotValue);
                                slotField.setAccessible(true);
                                slotFieldValue = slotField.get(value) == null ? "" : slotField.get(value).toString();
                            }
                            lineSb.append(slotFieldValue + ",");
                        } catch (Exception e) {
                            log.error("Can't get the field [{}] value", field.getName());
                            throw new IllegalArgumentException("Can't get the field value.");
                        }
                        break;
                    case METHOD:
                        String slotValue2 = excelLabeled.slotValue();
                        try {
                            Object value = field.get(line);
                            String slotMethodValue = "";
                            if (value != null) {
                                Method slotMethod = value.getClass().getMethod(slotValue2);
                                slotMethod.setAccessible(true);
                                Object result = slotMethod.invoke(value);
                                slotMethodValue = result == null ? "" : result.toString();
                            }
                            lineSb.append(slotMethodValue + ",");
                        } catch (Exception e) {
                            log.error("Can't get the field [{}] value", field.getName());
                            throw new IllegalArgumentException("Can't get the field value.");
                        }
                        break;
                }
            }
            longText.append(lineSb.substring(0, lineSb.length() - 1)).append("\n");
        }
        return longText.toString();
    }


    /**
     * Declare the excel column for the object type converted
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @Documented
    public @interface ExcelLabeled {
        /**
         * label name,excel column name
         *
         * @return
         */
        String value();

        /**
         * order for excel column
         *
         * @return
         */
        int order();

        /**
         * slot type, extents to display field value
         *
         * @return
         */
        SlotType slot() default SlotType.NONE;

        /**
         * slot value
         *
         * @return
         */
        String slotValue() default "";
    }

    public enum SlotType {
        NONE,
        FILED,
        METHOD
    }
}
