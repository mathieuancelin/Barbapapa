package com.foo.barbapapa;

import com.foo.barbapapa.BarbapapaTemplatesManager.ManagerHolder;
import com.foo.barbapapa.api.Template;
import com.foo.barbapapa.api.TypedTemplate;
import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheBuilder;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;
import java.io.File;
import java.io.Writer;

public class BarbapapaTypedTemplateImpl<T> implements TypedTemplate<T> {

    private Scope scope;
    private Mustache mustache;
    private MustacheBuilder builder;

    private ManagerHolder manager;
    
    private String name;
        
    BarbapapaTypedTemplateImpl(ManagerHolder manager) {
        this.manager = manager; 
    }
    
    BarbapapaTypedTemplateImpl(String name, ManagerHolder manager) {
        this.manager = manager;
        this.name = name;
        if (manager.isSet()) {
            select(name);
        }
    }
    
    public TypedTemplate select(String name) {
        this.builder = new MustacheBuilder(new File(manager.getBase()));
        try {
            this.mustache = builder.parseFile(name);
        } catch (MustacheException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public void writeTo(Writer wr) {
        try {
            if (builder == null) {
                select(name);
            }
            scope.put("root", manager.getRoot());
            FutureWriter writer = new FutureWriter(wr);
            mustache.execute(writer, scope);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TypedTemplate setModel(T model) {
        scope = new Scope(model);
        if (mustache == null) {
            Class<?> clazz = model.getClass();
            if (clazz.isAnnotationPresent(Template.class)) {
                Template t = clazz.getAnnotation(Template.class);
                select(t.value());
            }
        }
        return this;
    }
}
