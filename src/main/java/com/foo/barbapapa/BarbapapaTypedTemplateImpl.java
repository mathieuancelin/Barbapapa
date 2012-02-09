package com.foo.barbapapa;

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

    private BarbapapaTemplatesManager manager;
    
    private T model;
    
    BarbapapaTypedTemplateImpl(BarbapapaTemplatesManager manager) {
        this.manager = manager;
    }
    
    BarbapapaTypedTemplateImpl(String name, BarbapapaTemplatesManager manager) {
        this.manager = manager;
        select(name);
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
            scope.put("root", manager.getRoot());
            FutureWriter writer = new FutureWriter(wr);
            mustache.execute(writer, scope);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TypedTemplate setModel(T model) {
        scope = new Scope(model);
        this.model = model;
        return this;
    }
}
