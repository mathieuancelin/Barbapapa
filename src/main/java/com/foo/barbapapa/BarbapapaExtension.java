package com.foo.barbapapa;

import com.foo.barbapapa.BarbapapaTemplatesManager.MustacheTemplate;
import com.foo.barbapapa.BarbapapaTemplatesManager.Templates;
import com.foo.barbapapa.api.For;
import com.foo.barbapapa.api.Template;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

@ApplicationScoped
public class BarbapapaExtension implements Extension {
    
    private final Templates templates = new Templates();
    
    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery evt) {
        evt.addQualifier(For.class);
    }
    
    public void processAnnotatedType(@Observes ProcessAnnotatedType type) {
        if (type.getAnnotatedType().getJavaClass().isAnnotationPresent(Template.class)) {
            System.out.println("Class " + type.getAnnotatedType().getJavaClass() + " as new Template model");
            String name = ((Template) type.getAnnotatedType().getJavaClass().getAnnotation(Template.class)).value();
            templates.add(new MustacheTemplate(type.getAnnotatedType().getJavaClass(), name));
        }
    }
    
    public void afterDeployment(@Observes AfterBeanDiscovery evt, BeanManager mngr) {
        System.out.println("Sending templates metadata");
        mngr.fireEvent(templates);
    }
}
