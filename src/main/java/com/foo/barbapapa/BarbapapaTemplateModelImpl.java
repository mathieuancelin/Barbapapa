package com.foo.barbapapa;

import com.foo.barbapapa.BarbapapaTemplatesManager.ManagerHolder;
import com.foo.barbapapa.api.TemplateModel;
import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheBuilder;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;
import java.io.File;
import java.io.Writer;

public class BarbapapaTemplateModelImpl implements TemplateModel {
    
    private Scope scope;
    private Mustache mustache;
    private MustacheBuilder builder;
    private final String name;
    private ManagerHolder manager;
    
    public BarbapapaTemplateModelImpl(String name, ManagerHolder manager) throws MustacheException {
        this.name = name;
        this.manager = manager;
        this.scope = new Scope();
    }
   
    public TemplateModel attr(String name, Object o) {
        scope.put(name, o);
        return this;
    }
    
    public void writeTo(Writer wr) {
        try {
            if (builder == null) {
                builder = new MustacheBuilder(new File(manager.getBase()));
            }
            if (mustache == null) {
                mustache = builder.parseFile(name);
            }
            scope.put("root", manager.getRoot());
            FutureWriter writer = new FutureWriter(wr);
            mustache.execute(writer, scope);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
