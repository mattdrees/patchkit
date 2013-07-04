package org.cru.patchkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Matt Drees
 */
public class Jackson2JsonAdapter implements JsonAdapter{

    private final ObjectMapper mapper;

    public Jackson2JsonAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public JsonNode entityToTree(Object originalEntity) {
        return mapper.valueToTree(originalEntity);
    }

    @Override
    public <T> T treeToEntity(JsonNode updatedEntityAsJson, Class<T> originalEntityClass) {
        try {
            return mapper.treeToValue(updatedEntityAsJson, originalEntityClass);
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
