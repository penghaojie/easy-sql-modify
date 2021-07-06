package io.github.penghaojie.esm;


import net.sf.jsqlparser.statement.Statement;

public interface StatementProcessor {
    void process(Statement statement);

    void setSqlParser(DataPermissionSqlParser sqlParser);

    void setSqlCondition(SqlCondition sqlCondition);
}
