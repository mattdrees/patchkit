package org.cru.patchkit;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Matt Drees
 */
public class JsonAdapterException extends RuntimeException{
    public JsonAdapterException(Exception e) {
        super(e);
    }
}
