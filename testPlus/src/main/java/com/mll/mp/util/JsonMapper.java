package com.mll.mp.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mll.mp.pojo.User;

import java.util.List;

public class JsonMapper {
    private ObjectMapper mapper;

//    public JsonMapper(){
//        this((Include)null);
//    }
    public JsonMapper(Include include){
        this.mapper = new ObjectMapper();
        if (include!=null){
            mapper.setSerializationInclusion(include);
        }
        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static JsonMapper notEmptyMapper(){
        return new JsonMapper(Include.NON_EMPTY);
    }

    public static JsonMapper notNullMapper(){
        return new JsonMapper(Include.NON_NULL);
    }

    public static JsonMapper notDefaultMapper(){
        return new JsonMapper(Include.NON_DEFAULT);
    }

    //将对象序列化为json字符串
    public String toJson(Object o){
        try {
            String result = mapper.writeValueAsString(o);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //将json字符串反序列化为对象
    public <T> T fromJson(String jsonString,Class<T> clazz){
        if (!"".equals(jsonString)){
            try{
                return this.mapper.readValue(jsonString,clazz);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else
            return null;
    }

    //将json字符串反序列化为list，数组等
    public <T> T fromJson(String jsonString, JavaType javaType){
        if (!"".equals(jsonString)){
            try{
                return this.mapper.readValue(jsonString,javaType);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    //定义javatype
    public JavaType createJavaType(Class<?>collectionClass,Class<?>elementClass){
        return this.mapper.getTypeFactory().constructParametricType(collectionClass,elementClass);
    }

    //自定义将json转化为什么类型的，这个例子是转为List<User>
    public Object fromJsonToList(String jsonString){
        JavaType javaType = this.createJavaType(List.class, User.class);
        if (!"".equals(jsonString)){
            try{
                return mapper.readValue(jsonString,javaType);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else
            return null;
    }
}
