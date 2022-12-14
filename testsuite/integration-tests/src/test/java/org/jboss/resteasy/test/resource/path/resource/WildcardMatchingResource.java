package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("main")
public class WildcardMatchingResource {
    @GET
    public String subresource() {
        return this.getClass().getSimpleName();
    }
}
