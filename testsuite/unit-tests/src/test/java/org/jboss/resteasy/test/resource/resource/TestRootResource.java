/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("root")
public class TestRootResource {
    @GET
    public String get() {
        return "test";
    }
}
