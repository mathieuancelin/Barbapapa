package com.foo.barbapapa.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Template {
    String value();
    String mimeType() default "text/html";
}