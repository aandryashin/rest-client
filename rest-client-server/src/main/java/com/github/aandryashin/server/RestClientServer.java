package com.github.aandryashin.server;


import com.github.aandryashin.rest.Call;
import com.github.aandryashin.rest.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Component
@Path("/")
public class RestClientServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientServer.class);

    @Autowired
    HistoryService historyService;

    @Autowired
    HttpClientService httpClientService;

    @Path("call")
    @GET
    @Produces({
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON
    })
    public Call call() {
        return historyService.defaultCall();
    }

    @Path("call/{id}")
    @GET
    @Produces({
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON
    })
    public Call call(@PathParam("id") String id) {
        return historyService.getCallById(id);
    }

    @Path("call/{id}")
    @DELETE
    public void deleteCall(@PathParam("id") String id) {
        historyService.deleteCall(id);
    }

    @Path("calls")
    @GET
    @Produces({
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON
    })
    public Collection<Call> calls() {
        return historyService.getAllCalls();
    }

    @Path("request")
    @POST
    @Produces({
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON
    })
    @Consumes(MediaType.APPLICATION_XML)
    public Call requestXml(Call.Request request) {
        return requestJson(request);
    }

    @Path("request")
    @POST
    @Produces({
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON
    })
    @Consumes(MediaType.APPLICATION_JSON)
    public Call requestJson(Call.Request request) {
        return request(request);
    }

    public Call request(Call.Request request) {
        // Avoid POST requests with empty body
        if (request == null) {
            throw new WebApplicationException(Response.Status.UNSUPPORTED_MEDIA_TYPE);
        }
        Call call = new Call()
                .withId(UUID.randomUUID().toString())
                .withTimestamp(System.currentTimeMillis())
                .withRequest(request);
        try {
            call.withResponse(httpClientService.processCall(request));
        } catch (HttpClientServiceException e) {
            LOGGER.warn("{} {} - {}", request.getMethod(), request.getUrl(), e.getMessage());
            call.withError(new Call.Error().withMessage(e.getMessage()));
        }
        historyService.createCall(call);
        return call;
    }
}
