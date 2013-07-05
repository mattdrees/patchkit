package org.cru.patchkit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import junit.framework.Assert;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.cru.patchkit.model.Member;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

/**
 * @author Matt Drees
 */
public class JaxRsProviderJsonAdapterTest {


    ResteasyProviderFactory factory = new ResteasyProviderFactory();
    {
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        factory.addMessageBodyWriter(provider);
        factory.addMessageBodyReader(provider);
    }
    ObjectMapper mapper = new ObjectMapper();


    JaxRsProviderJsonAdapter adapter;
    {
        adapter = new JaxRsProviderJsonAdapter(mapper, factory);
    }

    @Test
    public void testToJsonNode()
    {
        Member member = new Member();
        member.setId(3L);
        member.setName("joe");
        JsonNode jsonNode = adapter.entityToTree(member);

        Assert.assertEquals(3L, jsonNode.get("id").longValue());
        Assert.assertEquals("joe", jsonNode.get("name").textValue());
    }

    @Test
    public void testTreeToEntity()
    {
        Class<Member> entityType = Member.class;
        ObjectNode json = mapper.createObjectNode();
        json.put("id", 3l);
        json.put("name", "joe");

        Member member = adapter.treeToEntity(json, entityType);

        Assert.assertEquals(new Long(3L), member.getId());
        Assert.assertEquals("joe", member.getName());
    }

}
