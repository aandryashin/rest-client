package com.github.aandryashin.server;

import com.github.aandryashin.rest.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EcxeptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EcxeptionHandler.class);

    @Override
    public Response toResponse(Exception e) {
        Response.StatusType info;
        if (e instanceof WebApplicationException) {
            info = ((WebApplicationException) e).getResponse().getStatusInfo();
        } else {
            LOGGER.error("Internal error:", e);
            info = Response.Status.INTERNAL_SERVER_ERROR;
        }
        return Response.status(info.getStatusCode()).entity(
                new Call.Response.Status().withCode(info.getStatusCode()).withMessage(info.getReasonPhrase())).build();
    }
}
