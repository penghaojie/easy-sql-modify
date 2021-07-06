package io.github.penghaojie.mdpp;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;


import java.util.List;

public abstract class AbstractDataPermissionSqlParser implements DataPermissionSqlParser {
    private List<SqlCondition> conditionList;
    private StatementProcessor insertProcessor;
    private StatementProcessor deleteProcessor;
    private StatementProcessor updateProcessor;
    private StatementProcessor selectProcessor;

    public AbstractDataPermissionSqlParser() {
        selectProcessor = new SelectStatementProcessor();
    }

    public String parse(String originSql) {
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(originSql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
            return originSql;
        }
        if (statement instanceof Insert && supportInsert()) {
            processInsert(statement);
        } else if (statement instanceof Delete && supportDelete()) {
            processDelete(statement);
        } else if (statement instanceof Update && supportUpdate()) {
            processUpdate(statement);
        } else if (statement instanceof Select && supportSelect()) {
            processSelect(statement);
        }
        return statement.toString();
    }

    private void processInsert(Statement statement) {

    }

    private void processDelete(Statement statement) {

    }

    private void processUpdate(Statement statement) {

    }

    private void processSelect(Statement statement) {
        selectProcessor.setSqlParser(this);
        for (SqlCondition condition : conditionList) {
            selectProcessor.setSqlCondition(condition);
            selectProcessor.process(statement);
        }
    }


    public List<SqlCondition> getConditions() {
        return conditionList;
    }

    public void setConditions(List<SqlCondition> conditionList) {
        this.conditionList = conditionList;
    }
}
