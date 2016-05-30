package com.github.aandryashin.client;

import com.github.aandryashin.rest.Call;

import javax.ws.rs.core.GenericType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;

public class RestClientClient {


    public Call call() {
        return RestClientClientGenerated.root().call().getAsCallXml();
    }

    public Call call(String id) {
        return RestClientClientGenerated.root().callId(id).getAsCallXml();
    }

    public void deleteCall(String id) {
        RestClientClientGenerated.root().callId(id).delete();
    }

    public List<Call> calls() {
        return RestClientClientGenerated.root().calls().getAsXml(new GenericType<List<Call>>() {
        });
    }

    // Actually Call.Request is inner class, and it is not annotated with @XmlRootElement,
    // so we have to wrap it with JAXBElement for correct serialization.
    public Call request(Call.Request request) {
        return RestClientClientGenerated.root().request()
                .postXml(new JAXBElement<Call.Request>(new QName("request"), Call.Request.class, request), Call.class);
    }

}
