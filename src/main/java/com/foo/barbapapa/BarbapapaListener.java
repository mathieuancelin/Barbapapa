package com.foo.barbapapa;

import com.foo.barbapapa.BarbapapaTemplatesManager.RegisterTemplatesRoot;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class BarbapapaListener implements ServletContextListener {

    @Inject Event<RegisterTemplatesRoot> register; 
    @Inject Event<BarbapapaTemplatesManager.ComputeTemplates> compute;
    @Inject Event<BarbapapaInit> init;
    
    public void contextInitialized(ServletContextEvent sce) {
//        register.fire(new RegisterTemplatesRoot(
//                sce.getServletContext().getContextPath(), 
//                sce.getServletContext().getRealPath("/views")));
//        compute.fire(new BarbapapaTemplatesManager.ComputeTemplates());
        init.fire(
            new BarbapapaInit(
                sce.getServletContext().getContextPath(), 
                sce.getServletContext().getRealPath("/views")
            )
        );
    }

    public void contextDestroyed(ServletContextEvent sce) { }
    
    public void initBarbapapa(@Observes BarbapapaInit evt) {
        register.fire(new RegisterTemplatesRoot(evt.root, evt.base));
        compute.fire(new BarbapapaTemplatesManager.ComputeTemplates());
    }
    
    public static class BarbapapaInit {
        public final String root;
        public final String base;

        public BarbapapaInit(String root, String base) {
            this.root = root;
            this.base = base;
        }
    }
}
