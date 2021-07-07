package io.github.penghaojie.esm;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;

public class ValueWrapper {
    public static Expression wrap(Object value) {
        if (value instanceof String) {
            return new StringValue((String) value);
        } else if (value instanceof Integer) {
            return new LongValue((Integer) value);
        } else if (value instanceof Long) {
            return new LongValue((Long) value);
        } else if (value instanceof Double) {
            return new DoubleValue( value.toString() );
        }
        throw new NotSupportValueType(value==null?null:value.getClass());
    }
}
