package com.foo.barbapapa;

import com.foo.barbapapa.BarbapapaTemplatesManager.BarbapapaInit;
import com.foo.barbapapa.IndexTemplate.Item;
import com.foo.barbapapa.api.For;
import com.foo.barbapapa.api.TemplateBuilder;
import com.foo.barbapapa.api.TemplateModel;
import com.foo.barbapapa.api.TypedTemplate;
import com.google.common.io.Files;
import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheBuilder;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AppTest {
    
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive arch = ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addPackage(BarbapapaExtension.class.getPackage())
            .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
        return arch;
    }
    
    public static final BarbapapaInit initEvt = 
            new BarbapapaInit("fake", "src/test/resources/templates");
    public static final List<Item> items = 
            Arrays.asList(new Item[] {new Item("Item1"), new Item("Item2")});

    @Inject Event<BarbapapaInit> init;
    
    @Inject IndexTemplate template;
        
    @Inject @For("index.html") TemplateModel model;
    
    @Inject TemplateBuilder builder;
    
    @Inject @For("index.html") TypedTemplate<IndexTemplate> selectedTyped;
    
    @Inject TypedTemplate<IndexTemplate> typed;
    
    @Before
    public void init() {
        init.fire(initEvt);
    }    

    @Test
    public void testBarbapapa() {
        StringWriter sw = new StringWriter();
        
        template.title("Items").addItem("Item1").addItem("Item2");
        template.writeTo(sw);
        
        assertSameTemplates(sw);
    }
        
    @Test
    public void testTemplateModel() {
        StringWriter sw = new StringWriter();
        
        model.attr("title", "Items").attr("items", items);
        model.writeTo(sw);
        
        assertSameTemplates(sw);
    }
    
    @Test
    public void testTemplateBuilder() {
        StringWriter sw = new StringWriter();

        builder.select("index.html").attr("title", "Items").attr("items", items);
        builder.writeTo(sw);
        
        assertSameTemplates(sw);
    }
    
    @Test
    public void testSelectedTypedTemplate() {
        StringWriter sw = new StringWriter();

        selectedTyped.setModel(new IndexTemplate().title("Items").addItem("Item1").addItem("Item2"));
        selectedTyped.writeTo(sw);
        
        assertSameTemplates(sw);
    }
    
    @Test
    public void testTypedTemplate() {
        StringWriter sw = new StringWriter();

        typed.select("index.html").setModel(new IndexTemplate().title("Items").addItem("Item1").addItem("Item2"));
        typed.writeTo(sw);
        
        assertSameTemplates(sw);
    }
    
    @Test
    public void testAutoTypedTemplate() {
        StringWriter sw = new StringWriter();

        typed.setModel(new IndexTemplate().title("Items").addItem("Item1").addItem("Item2"));
        typed.writeTo(sw);
        
        assertSameTemplates(sw);
    }
    
    @Test
    public void testMustache() throws Exception {
        MustacheBuilder builder = new MustacheBuilder(new File("src/test/resources/templates"));
        Mustache mustache = builder.parseFile("index.html");
        Scope scope = new Scope(new IndexTemplate().title("Items").addItem("Item1").addItem("Item2"));
        StringWriter sw = new StringWriter();
        FutureWriter writer = new FutureWriter(sw);
        mustache.execute(writer, scope);
        String result = sw.toString();
        System.out.println(result);
        String expected = Files.toString(new File("src/test/resources/templates/result_1.html"), Charset.forName("utf-8"));
        Assert.assertEquals(expected, result);
    }
    
    public void assertSameTemplates(StringWriter writer) {
        try {
            String result = writer.toString();
            System.out.println(result);
            String expected = Files.toString(new File("src/test/resources/templates/result.html"), Charset.forName("utf-8"));
            Assert.assertEquals(expected, result);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
