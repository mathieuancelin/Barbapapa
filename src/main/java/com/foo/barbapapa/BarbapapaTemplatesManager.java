package com.foo.barbapapa;

import com.foo.barbapapa.api.*;
import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheBuilder;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class BarbapapaTemplatesManager {
    
    private MustacheBuilder builder;
    
    private String root;
    
    private String base;
    
    private Map<Class<?>, Mustache> templates = new HashMap<Class<?>, Mustache>();
    
    private List<MustacheTemplate> templatesDef;
    
    @Produces @For("")
    public TemplateModel getModel(InjectionPoint p) throws MustacheException {
        For f = findAnnotation(p.getQualifiers());
        if (f != null) {
            return new BarbapapaTemplateModelImpl(f.value(), this);
        }
        throw new RuntimeException("Unable to find template");
    }
    
    @Produces
    public TemplateBuilder getBuilder() {
        return new BarbapapaTemplateBuilderImpl(this);
    }
    
    @Produces @For("")
    public SelectedTemplateBuilder getSelectedBuilder(InjectionPoint p) {
        For f = findAnnotation(p.getQualifiers());
        if (f != null) {
            return new BarbapapaTemplateBuilderImpl(f.value(), this);
        }
        throw new RuntimeException("Unable to find template");
    }
    
    @Produces
    public <T> TypedTemplate<T> getTypedTemplate() {
        return new BarbapapaTypedTemplateImpl<T>(this);
    }
    
    @Produces @For("")
    public <T> TypedTemplate<T> getSelectedTypedTemplate(InjectionPoint p) {
        For f = findAnnotation(p.getQualifiers());
        if (f != null) {
            return new BarbapapaTypedTemplateImpl<T>(f.value(), this);
        }
        throw new RuntimeException("Unable to find template");
    }
       
    public void registerRoot(@Observes RegisterTemplatesRoot reg) {
        System.out.println("Registering templates root");
        base = reg.getViewDirectory();
        builder = new MustacheBuilder(new File(reg.getViewDirectory()));
        root = reg.getContextPath();
    }
    
    public void registerTemplates(@Observes Templates comp) throws MustacheException {
        System.out.println("Registering templates metadata");
        templatesDef = comp.getTemplates();
    }
    
    public void compileTemplates(@Observes ComputeTemplates comp) throws MustacheException {
        System.out.println("Templates computation ...");
        for (MustacheTemplate template : templatesDef) {
            System.out.println("Compiling template : " + template.name);
            templates.put(template.type, builder.parseFile(template.name));
        }
        System.out.println("Ready to go !!!");
    }
    
    public boolean isWritable(Class<?> type, String media) {
        if (templates.containsKey(type)) {
            return true;
        }
        for(Class<?> clazz : templates.keySet()) {
            if (clazz.isAssignableFrom(type)) {
                templates.put(type, templates.get(clazz));
                return true;
            }
        }
        return false;
    }
    
    public Mustache getTemplate(Class<?> type, String media) {
        if (templates.containsKey(type)) {
            return templates.get(type);
        }
        for(Class<?> clazz : templates.keySet()) {
            if (clazz.isAssignableFrom(type)) {
                templates.put(type, templates.get(clazz));
                return templates.get(clazz);
            }
        }
        return null;
    }
    
    public void writeTo(Object o, Mustache mustache, Writer os) throws MustacheException, IOException {
        Scope scope = new Scope(o);
        scope.put("root", root);
        FutureWriter writer = new FutureWriter(os);
        mustache.execute(writer, scope);
    }
    
    public void writeExistingTo(Object o, Class<?> template, Writer os) throws MustacheException, IOException {
        Scope scope = new Scope(o);
        scope.put("root", root);
        FutureWriter writer = new FutureWriter(os);
        getTemplate(template, "*/*").execute(writer, scope);
    }

    public String getRoot() {
        return root;
    }

    public String getBase() {
        return base;
    }

    private For findAnnotation(Set<Annotation> qualifiers) {
        for (Annotation anno : qualifiers) {
            if (anno.annotationType().equals(For.class)) {
                return (For) anno;
            }
        }
        return null;
    }
    
    public static class RegisterTemplatesRoot {
        private final String path;
        private final String views;

        public RegisterTemplatesRoot(String path, String views) {
            this.path = path;
            this.views = views;
        }
        
        public String getContextPath() {
            return path;
        }
        
        public String getViewDirectory() {
            return views;
        }
    }
    
    public static class ComputeTemplates {
        
    }
    
    public static class Templates {
        
        public final List<MustacheTemplate> templates = new ArrayList<MustacheTemplate>();
        
        public void add(MustacheTemplate template) {
            templates.add(template);
        }
        
        public List<MustacheTemplate> getTemplates() {
            return templates;
        }
    }
    
    public static class MustacheTemplate {
        public final Class<?> type;
        public final String name;

        public MustacheTemplate(Class<?> type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}
