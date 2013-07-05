package org.cru.patchkit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

/**
 * @author Matt Drees
 */
public class JaxRsProviderJsonAdapter implements JsonAdapter {

    Providers providers;

    ObjectMapper mapper;

    public JaxRsProviderJsonAdapter(ObjectMapper mapper, Providers providers) {
        this.mapper = mapper;
        this.providers = providers;
    }

    @Override
    public JsonNode entityToTree(Object entity) {

        byte[] bytes = writeToBytes(entity);
        try {
            return mapper.readTree(bytes);
        } catch (IOException e) {
            throw new JsonAdapterException(e);
        }
    }

    private <T> byte[] writeToBytes(T entity)
    {
        Class<T> type = (Class<T>) entity.getClass();
        Class<?> genericType = entity.getClass();
        Annotation[] annotations = {};
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        MessageBodyWriter<T> writer = providers.getMessageBodyWriter(type, genericType, annotations, mediaType);
        MultivaluedMap<String, Object> httpHeaders = null;

        ByteArrayOutputStream entityStream = new ByteArrayOutputStream();
        try {
            writer.writeTo(entity, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        } catch (IOException e) {
            throw new JsonAdapterException(e);
        }
        return entityStream.toByteArray();
    }

    @Override
    public <T> T treeToEntity(JsonNode json, Class<T> entityType) {
        byte[] bytes = writeJsonToBytes(json);

        Class<?> genericType = entityType;
        Annotation[] annotations = {};
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        MessageBodyReader<T> reader = providers.getMessageBodyReader(entityType, genericType, annotations, mediaType);

        MultivaluedMap<String, String> httpHeaders = null;
        ByteArrayInputStream entityStream = new ByteArrayInputStream(bytes);
        try {
            return reader.readFrom(entityType, genericType, annotations, mediaType, httpHeaders, entityStream);
        } catch (IOException e) {
            throw new JsonAdapterException(e);
        }
    }

    private byte[] writeJsonToBytes(JsonNode json) {
        try {
            return mapper.writeValueAsBytes(json);
        } catch (JsonProcessingException e) {
            throw new JsonAdapterException(e);
        }
    }

    @Override
    public String printForDebug(JsonNode entityAsJson) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(entityAsJson);
        } catch (JsonProcessingException e) {
            throw new JsonAdapterException(e);
        }
    }
}
