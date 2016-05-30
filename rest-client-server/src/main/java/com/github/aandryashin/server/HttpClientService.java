package com.github.aandryashin.server;

import com.github.aandryashin.rest.Call;
import com.github.aandryashin.rest.Payload;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class HttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);

    public Call.Response processCall(Call.Request callRequest) {
        try (CloseableHttpClient client = newHttpClient();
             CloseableHttpResponse response = client.execute(convert(callRequest))) {
            return convert(response);
        } catch (Exception e) {
            throw new HttpClientServiceException(e);
        }
    }

    protected CloseableHttpClient newHttpClient() {
        return HttpClientBuilder.create().build();
    }

    protected HttpUriRequest convert(Call.Request callRequest) {

        RequestBuilder builder = RequestBuilder.create(callRequest.getMethod().value()).setUri(callRequest.getUrl());

        if (callRequest.getBody() != null) {
            builder.setEntity(new StringEntity(callRequest.getBody(), Charset.forName("UTF-8")));
        }
        for (Payload.Header header : callRequest.getHeaders()) {
            builder.addHeader(header.getName(), header.getValue());
        }
        return builder.build();
    }

    protected Call.Response convert(CloseableHttpResponse response) {
        try {
            String body = "";
            if (response.getEntity() != null) {
                body = IOUtils.toString(
                        response.getEntity().getContent(),
                        response.getEntity().getContentEncoding() != null ?
                                Charset.forName(response.getEntity().getContentEncoding().getValue()) :
                                Charset.defaultCharset()
                );
            }
            Call.Response callResponse = new Call.Response().withHeaders().withStatus(
                    new Call.Response.Status()
                            .withCode(response.getStatusLine().getStatusCode())
                            .withMessage(response.getStatusLine().getReasonPhrase()))
                    .withBody(body);
            for (Header header : response.getAllHeaders()) {
                callResponse.getHeaders().add(
                        new Payload.Header().withName(header.getName()).withValue(header.getValue()));
            }
            return callResponse;

        } catch (IOException e) {
            throw new HttpClientServiceException(e);
        }
    }
}