package io.github.penghaojie.esm;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;
import java.util.List;

public class SelectStatementProcessor implements StatementProcessor {
    private DataPermissionSqlParser sqlParser;
    private SqlCondition sqlCondition;

    public void process(Statement statement) {
        if (!(statement instanceof Select && sqlParser.supportSelect())) {
            return;
        }
        Select select = (Select)statement;
        SelectBody selectBody = select.getSelectBody();
        process(selectBody);
    }

    private void process(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                process(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList)selectBody;
            List<SelectBody> selects = operationList.getSelects();
            if (selects != null && !selects.isEmpty()) {
                for (SelectBody body : selects) {
                    process(body);
                }
            }
        }
    }

    public void setSqlParser(DataPermissionSqlParser sqlParser) {
        this.sqlParser = sqlParser;
    }

    public void setSqlCondition(SqlCondition sqlCondition) {
        this.sqlCondition = sqlCondition;
    }

    // 处理简单查询
    private void processPlainSelect(PlainSelect plainSelect) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table)fromItem;
            if (!sqlParser.doTableFilter(fromTable.getName())) {
                return;
            }
            if (sqlParser.doTableFilter(fromTable.getName())) {
                plainSelect.setWhere(buildExpression(plainSelect.getWhere(),fromTable));
            }
        }else{
            processFromItem(fromItem);
        }
        List<Join> joins = plainSelect.getJoins();

        if (joins != null && joins.size() > 0) {
            for (Join join : joins) {
                processJoin(join);
                processFromItem(join.getRightItem());
                Expression onExpression = join.getOnExpression();
                Expression where = plainSelect.getWhere();
                AndExpression andExpression = new AndExpression(onExpression, where);
                plainSelect.setWhere(andExpression);
            }
        }
    }

    //构建sql表达式
    private Expression buildExpression(Expression expression, Table table) {
        StringBuilder builder = new StringBuilder();
        if (table != null) {
            builder.append(table.getAlias() != null ? table.getAlias() : table.getName());
            builder.append(".");
        }
        builder.append(sqlCondition.getColumnName());
        Column permissionColumn = new Column(builder.toString());

        // 根据不同的条件类型，构建不同的表达式
        ConditionEnum type = sqlCondition.conditionType();
        Expression retExp;
        switch (type) {
            case EQUAL:
                retExp = buildEqualExpression(permissionColumn);
                break;
            case IN:
                retExp = buildInExpression(permissionColumn);
                break;
            default:
                retExp = null;
        }
        if (expression == null) {
            return retExp;
        }else{
            return new AndExpression(retExp, expression);
        }
    }

    private Expression buildEqualExpression(Expression left) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(left);
        Object columnValue = sqlCondition.getColumnValue();
        Expression rightExp = ValueWrapper.wrap(columnValue);
        equalsTo.setRightExpression(rightExp);
        return equalsTo;
    }

    private Expression buildInExpression(Expression left) {
        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(left);
        List<Object> columnValues = sqlCondition.getColumnValues();
        ExpressionList expressionList = new ExpressionList();
        ArrayList<Expression> list = new ArrayList<>();
        for (Object value : columnValues) {
            list.add(ValueWrapper.wrap(value));
        }
        expressionList.setExpressions(list);
        inExpression.setRightItemsList(expressionList);
        return inExpression;
    }

    private void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            if (sqlParser.doTableFilter(fromTable.getName())) {
                join.setOnExpression(buildExpression(join.getOnExpression(),fromTable));
            }
        }
    }

    private void processFromItem(FromItem fromItem) {
        if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            List<Join> joinList = subJoin.getJoinList();
            for (Join join : joinList) {
                processJoin(join);
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                process(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {

        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            SubSelect subSelect = lateralSubSelect.getSubSelect();
            if (subSelect != null) {
                if (subSelect.getSelectBody() != null) {
                    process(subSelect.getSelectBody());
                }
            }
        }
    }
}
