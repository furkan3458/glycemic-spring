package com.glycemic.serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.glycemic.model.Food;;

public class NutritionalSerializer extends StdSerializer<Food>{

	private static final long serialVersionUID = -2124290518222619914L;

	public NutritionalSerializer() {
		this(null);
    }

    public NutritionalSerializer(Class<Food> t) {
        super(t);
    }

    @Override
    public void serialize(
      Food items, 
      JsonGenerator generator, 
      SerializerProvider provider) 
      throws IOException, JsonProcessingException {
    	
        List<HashMap<String,Object>> ids = new ArrayList<>();
        HashMap<String,Object> hash = new HashMap<>();
    	hash.put("id", items.getId());
    	hash.put("name", items.getName());
    	hash.put("glycemicindex", items.getGlycemicIndex());
    	hash.put("image", items.getImage());
    	hash.put("source", items.getSource());
    	hash.put("url", items.getUrl());
    	hash.put("createdBy", items.getCreatedBy());
    	hash.put("createdDate", items.getCreatedDate());
    	hash.put("modifiedBy", items.getModifiedBy());
    	hash.put("modifiedDate", items.getModifiedDate());
    	hash.put("enabled", items.isEnabled());
    	ids.add(hash);
        generator.writeObject(ids);
    }
}