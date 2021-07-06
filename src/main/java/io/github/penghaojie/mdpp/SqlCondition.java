package io.github.penghaojie.mdpp;

import java.util.List;

/**
 * 需要拼接的SQL条件
 */
public interface SqlCondition {

    /**
     * 匹配表名
     * @param tableName 数据库表名称
     * @return true-匹配 false-不匹配
     */
    boolean tableMatch(String tableName);

    /**
     * @return 需要拼接的sql字段名称
     */
    String getColumnName();

    /**
     * @return sql条件类型
     */
    ConditionEnum conditionType();

    /**
     * @return sql字段值
     */
    String getColumnValue();

    /**
     * @return sql字段值范围
     */
    List<String> getColumnValues();
}
