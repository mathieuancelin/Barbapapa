package com.foo.barbapapa.integration;

import com.foo.barbapapa.BarbapapaTemplatesManager;
import com.foo.barbapapa.api.Writable;
import com.sampullara.mustache.MustacheException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class BarbapapaBodyWriter implements MessageBodyWriter<Object> {

    @Inject BarbapapaTemplatesManager manager;
    
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (Writable.class.isAssignableFrom(type)) {
            return true;
        }
        return manager.isWritable(type, mediaType.toString());
    }

    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            if (Writable.class.isAssignableFrom(type)) {
                Writable writable = (Writable) t;
                writable.writeTo(new OutputStreamWriter(entityStream));
            } else {
                manager.writeTo(t, manager.getTemplate(type, mediaType.toString()), new OutputStreamWriter(entityStream));
            }
            entityStream.flush();
        } catch (MustacheException ex) {
            ex.printStackTrace();
        }
    }
}
