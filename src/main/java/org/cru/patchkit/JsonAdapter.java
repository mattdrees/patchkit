package org.cru.patchkit;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Matt Drees
 *
 * abstracts the json <-> entity conversion
 */
public interface JsonAdapter {

    JsonNode entityToTree(Object originalEntity);

    <T> T treeToEntity(JsonNode updatedEntityAsJson, Class<T> originalEntityClass);

    String printForDebug(JsonNode entityAsJson);
}
