//package org.smallbox.faraway.client.debug.config;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//
//import javax.ws.rs.ext.ContextResolver;
//import javax.ws.rs.ext.Provider;
//
//@Provider
//public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
//
//    final ObjectMapper defaultObjectMapper;
//
//    public ObjectMapperProvider() {
//        defaultObjectMapper = createDefaultMapper();
//        defaultObjectMapper .setSerializationInclusion(JsonInclude.Include.NON_NULL);
//    }
//
//    @Override
//    public ObjectMapper getContext(Class<?> type) {
//        return defaultObjectMapper;
//    }
//
//    private static ObjectMapper createDefaultMapper() {
//        final ObjectMapper result = new ObjectMapper();
//        result.configure(SerializationFeature.INDENT_OUTPUT, true);
//
//        return result;
//    }
//}
