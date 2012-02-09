package com.foo.barbapapa;

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
    private String root;
    
    private final String name;
    
    private BarbapapaTemplatesManager manager;
    
    public BarbapapaTemplateModelImpl(String name, BarbapapaTemplatesManager manager) throws MustacheException {
        this.name = name;
        this.manager = manager;
        this.scope = new Scope();
        this.builder = new MustacheBuilder(new File(manager.getBase()));
        mustache = builder.parseFile(name);
        this.root = manager.getRoot();
    }
   
    public TemplateModel attr(String name, Object o) {
        scope.put(name, o);
        return this;
    }
    
    public void writeTo(Writer wr) {
        try {
            scope.put("root", root);
            FutureWriter writer = new FutureWriter(wr);
            mustache.execute(writer, scope);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
