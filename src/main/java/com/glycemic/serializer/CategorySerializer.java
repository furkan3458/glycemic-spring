package com.glycemic.serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.glycemic.model.Food;

public class CategorySerializer extends StdSerializer<List<Food>>{

	private static final long serialVersionUID = -2124290518222619914L;

	public CategorySerializer() {
		this(null);
    }

    public CategorySerializer(Class<List<Food>> t) {
        super(t);
    }

    @Override
    public void serialize(
      List<Food> items, 
      JsonGenerator generator, 
      SerializerProvider provider) 
      throws IOException, JsonProcessingException {
    	
        List<HashMap<String,Object>> ids = new ArrayList<>();
        for (Food item : items) {
        	HashMap<String,Object> hash = new HashMap<>();
        	hash.put("gid", item.getId());
        	hash.put("name", item.getName());
        	hash.put("glycemicindex", item.getGlycemicIndex());
        	hash.put("image", item.getImage());
        	hash.put("source", item.getSource());
        	hash.put("url", item.getUrl());
        	hash.put("createdBy", item.getCreatedBy());
        	hash.put("createdDate", item.getCreatedDate());
        	hash.put("modifiedBy", item.getModifiedBy());
        	hash.put("modifiedDate", item.getModifiedDate());
        	hash.put("enabled", item.isEnabled());
        	ids.add(hash);
        }
        generator.writeObject(ids);
    }
}