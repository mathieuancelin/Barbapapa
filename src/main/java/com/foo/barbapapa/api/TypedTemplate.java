package com.foo.barbapapa.api;

public interface TypedTemplate<T> extends Writable {
    public TypedTemplate select(String name);
    public TypedTemplate setModel(T model);
}
