package com.github.aandryashin.client;

import com.github.aandryashin.rest.Call;
import com.github.aandryashin.rest.Method;
import com.github.aandryashin.rest.Payload;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringEndsWith.endsWith;

public class RestClientServerTest {

    @ClassRule
    public static TestRule cleanUp = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            cleanUp();
        }

        @Override
        protected void after() {
            cleanUp();
        }
    };

    // This case looks like Jersey issue...
    @Test
    public void postRequestWithEmptyBody() throws Exception {
        HttpResponse response = Request.Post("http://localhost:8080/api/request").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode(), equalTo(415));
        assertThat(response.getStatusLine().getReasonPhrase(), equalTo("Unsupported Media Type"));
    }

    @Test
    public void throwErrorOnServer() throws Exception {
        HttpResponse response = Request.Get("http://localhost:8080/api/test/exception").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode(), equalTo(500));
        assertThat(response.getStatusLine().getReasonPhrase(), equalTo("Request failed."));
    }

    @Test
    public void brokenUrl() throws Exception {
        Call.Request request = new Call.Request().withUrl("url");

        assertThat(new RestClientClient()
                .request(request).getError().getMessage(), endsWith("Target host is not specified"));
    }

    @Test
    public void connectionRefused() throws Exception {
        Call.Request request = new Call.Request().withUrl("http://localhost:65535");

        assertThat(new RestClientClient()
                .request(request).getError().getMessage(), endsWith("Connection refused"));
    }

    @Test
    public void requestWithBody() throws Exception {
        Call.Request request = new Call.Request().withUrl("http://localhost:8080/api/test/post").withMethod(Method.POST);

        assertThat(new RestClientClient()
                .request(request).getResponse().getStatus().getCode(), equalTo(200));

        assertThat(new RestClientClient()
                .request(request.withBody("AbcАбв")).getResponse().getBody(), equalTo("AbcАбв"));
    }

    @Test
    public void requestWithHeaders() throws Exception {
        Call.Request request = new Call.Request().withUrl("http://localhost:8080/api/test/ok").withMethod(Method.GET)
                .withHeaders(new Payload.Header().withName("Accept").withValue("application/json"));

        assertThat(new RestClientClient()
                .request(request).getResponse().getStatus().getCode(), equalTo(200));

    }

    @Test
    public void requestWithAuth() throws Exception {
        Call.Request request = new Call.Request().withUrl("http://localhost:8080/api/test/auth").withMethod(Method.GET);

        assertThat(new RestClientClient().request(request.withAuth(new Call.Request.Auth()))
                        .getResponse().getStatus().getCode(),
                equalTo(401)
        );
        assertThat(new RestClientClient().request(request.withAuth(new Call.Request.Auth().withUser("   ")))
                        .getResponse().getStatus().getCode(),
                equalTo(401)
        );
        assertThat(new RestClientClient().request(request.withAuth(new Call.Request.Auth().withUser("user")))
                        .getResponse().getStatus().getCode(),
                equalTo(200)
        );
        assertThat(new RestClientClient().request(request.withAuth(new Call.Request.Auth().withToken("   ")))
                        .getResponse().getStatus().getCode(),
                equalTo(401)
        );
        assertThat(new RestClientClient().request(request.withAuth(new Call.Request.Auth().withToken("token")))
                        .getResponse().getStatus().getCode(),
                equalTo(200)
        );

    }

    private static void cleanUp() {
        for (Call call : new RestClientClient().calls()) {
            new RestClientClient().deleteCall(call.getId());
        }
    }

}
