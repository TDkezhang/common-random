package com.apifan.common.random.util;

import com.apifan.common.random.entity.DataField;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据生成工具
 * <p>用于批量快速生成随机虚拟数据</p>
 *
 * @author yin
 * @since 1.0.10
 */
public class DataUtils {
    private static final Logger logger = LoggerFactory.getLogger(DataUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成JSON
     *
     * @param fieldList 数据字段定义
     * @param total     数量
     * @return JSON字符串
     */
    public static String generateJson(List<DataField> fieldList, int total) {
        List<String> strList = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            strList.add(generateJson(fieldList));
        }
        return "[" + Joiner.on(",").join(strList) + "]";
    }

    /**
     * 过滤无效的数据字段定义
     *
     * @param fieldList 数据字段定义
     * @return 保留有效的数据字段定义
     */
    private static List<DataField> skipInvalidFields(List<DataField> fieldList) {
        if (CollectionUtils.isEmpty(fieldList)) {
            throw new RuntimeException("数据字段定义为空!");
        }
        List<DataField> validList = fieldList.stream().filter(Objects::nonNull).filter(i -> StringUtils.isNotBlank(i.getField()) && i.getValueSupplier() != null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(validList)) {
            throw new RuntimeException("有效的数据字段定义为空!");
        }
        return validList;
    }

    /**
     * 生成单个JSON对象字符串
     *
     * @param fieldList 数据字段定义
     * @return JSON对象字符串
     */
    private static String generateJson(List<DataField> fieldList) {
        fieldList = skipInvalidFields(fieldList);
        Map<String, Object> element = new LinkedHashMap<>();
        fieldList.forEach(f -> {
            if (f == null || StringUtils.isBlank(f.getField()) || f.getValueSupplier() == null) {
                return;
            }
            element.put(f.getField(), f.getValueSupplier().get());
        });
        try {
            return objectMapper.writeValueAsString(element);
        } catch (JsonProcessingException e) {
            logger.error("转换JSON字符串时出错", e);
        }
        return null;
    }
}
