package com.foo.barbapapa.integration;

import com.foo.barbapapa.BarbapapaTemplatesManager.BarbapapaInit;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class BarbapapaListener implements ServletContextListener {

    @Inject Event<BarbapapaInit> init;
    
    public void contextInitialized(ServletContextEvent sce) {
        init.fire(
            new BarbapapaInit(
                sce.getServletContext().getContextPath(), 
                sce.getServletContext().getRealPath("/views")
            )
        );
    }

    public void contextDestroyed(ServletContextEvent sce) { }

}
