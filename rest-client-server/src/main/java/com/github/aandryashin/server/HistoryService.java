package com.github.aandryashin.server;

import com.github.aandryashin.rest.Call;
import com.github.aandryashin.rest.Method;
import com.github.aandryashin.rest.Payload;
import com.hazelcast.core.IMap;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class HistoryService {

    @Resource(name = "history")
    private IMap<String, Call> history;

    public Call defaultCall() {
        return new Call().withRequest(new Call.Request().withMethod(Method.GET)
                .withHeaders(new ArrayList<Payload.Header>()).withAuth(new Call.Request.Auth())
        );
    }

    public Call getCallById(String id) {
        return history.get(id);
    }

    public void deleteCall(String id) {
        history.remove(id);
    }

    public Collection<Call> getAllCalls() {
        return history.values();
    }

    public void createCall(Call call) {
        history.put(call.getId(), call);
    }
}
