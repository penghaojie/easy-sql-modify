package io.github.penghaojie.esm;

import java.util.List;

/**
 * 数据权限SQL解析器
 */
public interface DataPermissionSqlParser {

    /**
     * 根据表名判断是否需要进行SQL处理
     * @param tableName 表名
     * @return true-处理 false-不处理
     */
    boolean doTableFilter(String tableName);

    String parse(String originSql);

    /**
     * 需要拼接的sql条件
     */
    List<SqlCondition> getConditions();

    void setConditions(List<SqlCondition> conditionList);

    /**
     * @return 是否支持解析Insert语句
     */
    boolean supportInsert();

    /**
     * @return 是否支持解析Delete语句
     */
    boolean supportDelete();

    /**
     * @return 是否支持解析Update语句
     */
    boolean supportUpdate();

    /**
     * @return 是否支持解析Select语句
     */
    boolean supportSelect();
}
