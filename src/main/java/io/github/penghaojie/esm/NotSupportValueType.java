package io.github.penghaojie.esm;

public class NotSupportValueType extends RuntimeException {
    public NotSupportValueType(Class<?> cls) {
        super("不支持类型为"+cls+"值");
    }
}
