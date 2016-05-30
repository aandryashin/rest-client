package com.github.aandryashin.server;

import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


// Testing http resource with known responses, for use with integration tests.

@Component
@Path("/test")
public class RestClientTestServer {

    @GET
    @Path("ok")
    public Response ok() {
        return Response.ok().build();
    }

    @GET
    @Path("auth")
    public Response basic(@HeaderParam("Authorization") String auth) {
        if(auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("error")
    public Response error() {
        return Response.serverError().build();
    }

    @POST
    @Path("post")
    @Produces({MediaType.TEXT_PLAIN})
    @Consumes(MediaType.TEXT_PLAIN)
    public Response post(String body) {
        return Response.ok().entity(body).build();
    }

    @GET
    @Path("exception")
    public Response exception() throws Exception {
        throw new Exception("Test Exception");
    }

}
