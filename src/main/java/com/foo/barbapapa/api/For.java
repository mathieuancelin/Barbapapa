package com.foo.barbapapa.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface For {
    
    @Nonbinding String value();
    
    public static class ForQualifier extends AnnotationLiteral<For> implements For {

        private final String value;
        
        public ForQualifier(String value) {
            this.value = value;
        }
        
        public String value() {
            return value;
        }
    }
}