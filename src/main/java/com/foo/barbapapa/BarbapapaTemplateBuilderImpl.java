package com.foo.barbapapa;

import com.foo.barbapapa.BarbapapaTemplatesManager.ManagerHolder;
import com.foo.barbapapa.api.SelectedTemplateBuilder;
import com.foo.barbapapa.api.TemplateBuilder;
import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheBuilder;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;
import java.io.File;
import java.io.Writer;

public class BarbapapaTemplateBuilderImpl implements TemplateBuilder, SelectedTemplateBuilder {

    private final Scope scope;
    private Mustache mustache;
    private MustacheBuilder builder;
    private String name;
    private ManagerHolder manager;
    
    BarbapapaTemplateBuilderImpl(ManagerHolder manager) {
        this.scope = new Scope();
        this.manager = manager;
    }
    
    BarbapapaTemplateBuilderImpl(String name, ManagerHolder manager) {
        this.scope = new Scope();
        this.manager = manager;
        this.name = name;
        if (manager.isSet()) {
            select(name);
        }
    }
    
    public SelectedTemplateBuilder select(String name) {
        this.builder = new MustacheBuilder(new File(manager.getBase()));
        try {
            this.mustache = builder.parseFile(name);
        } catch (MustacheException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public SelectedTemplateBuilder attr(String name, Object o) {
        scope.put(name, o);
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
}
