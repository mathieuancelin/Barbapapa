package com.foo.barbapapa;

import com.foo.barbapapa.api.Template;
import com.foo.barbapapa.api.Writable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

@Template("index.html")
public class IndexTemplate implements Writable {

    private String title;
    private List<Item> items;
    
    @Inject BarbapapaTemplatesManager manager;

    public IndexTemplate title(String title) {
        this.title = title;
        return this;
    }

    public String title() {
        return title;
    }

    public IndexTemplate addItem(String value) {
        if (items == null) {
            items = new ArrayList<Item>();
        }
        items.add(new Item(value));
        return this;
    }

    public List<Item> items() {
        return items;
    }
    
    @Override
    public void writeTo(Writer wr) {
        try {
            manager.writeExistingTo(this, getClass(), wr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class Item {

        public final String item;

        public Item(String item) {
            this.item = item;
        }
    }
}
