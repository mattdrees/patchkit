package org.cru.patchkit;

import org.cru.patchkit.model.Member;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JsonPatchRequestTest {


    Logger log = Logger.getLogger(getClass().getName());

    JsonPatchRequestReader reader = new JsonPatchRequestReader();

    public Member createJane(){
        Member member = new Member();
        member.setName("Jane Doe");
        member.setEmail("jane@mailinator.com");
        member.setPhoneNumber("2125551234");
        return member;
    }

    Member member = createJane();

    @Test
    public void testPatchApplicationWhenNameUpdateIsSuccessful() throws Exception {
        String patch =
                "[{" +
                "\"op\": \"replace\", " +
                "\"path\": \"/name\"," +
                "\"value\": \"Jenny Doe\"" +
                "}]";

        Member updatedMember = reader.buildJsonPatchRequest(patch).apply(member);

        assertEquals("Jenny Doe", updatedMember.getName());
    }

    @Test
    public void testPatchApplicationWhenPatchDoesNotParse() throws Exception {
        String patch = "garbage";

        try {
            reader.buildJsonPatchRequest(patch);
            fail("should have thrown exception");
        }
        catch (WebApplicationException e)
        {
            assertEquals(400, e.getResponse().getStatus());
            log.fine(e.getResponse().getEntity().toString());
        }
    }

    @Test
    public void testPatchApplicationWhenPatchIsJsonButNotJsonPatch() throws Exception {
        String patch =
                "[{\"foo\": \"bar\"}]";

        try {
            reader.buildJsonPatchRequest(patch);
            fail("should have thrown exception");
        }
        catch (WebApplicationException e)
        {
            assertEquals(400, e.getResponse().getStatus());
            log.info(e.getResponse().getEntity().toString());
        }
    }

    @Test
    public void testPatchApplicationWhenPatchIsValidJsonPatchButPatchErrors() throws Exception {

        String patch =
                "[{" +
                "\"op\": \"replace\", " +
                "\"path\": \"/notARealPath\"," +
                "\"value\": \"something\"" +
                "}]";

        JsonPatchRequest request = reader.buildJsonPatchRequest(patch);
        try {
            request.apply(member);
            fail("should have thrown exception");
        }
        catch (WebApplicationException e)
        {
            assertEquals(409, e.getResponse().getStatus());
            log.fine(e.getResponse().getEntity().toString());
        }
    }


    @Test
    public void testPatchApplicationWhenPatchIsValidJsonPatchWithoutConflictButResultIsNotAValidEntity() throws Exception {

        String patch =
                "[{" +
                        "\"op\": \"add\", " +
                        "\"path\": \"/anUnsupportedAttribute\"," +
                        "\"value\": \"something\"" +
                        "}]";

        JsonPatchRequest request = reader.buildJsonPatchRequest(patch);
        try {
            request.apply(member);
            fail("should have thrown exception");
        }
        catch (WebApplicationException e)
        {
            assertEquals(422, e.getResponse().getStatus());
            log.fine(e.getResponse().getEntity().toString());
        }
    }

}
