package com.github.aandryashin.client;

import com.github.aandryashin.rest.Call;
import com.github.aandryashin.rest.Method;
import com.github.aandryashin.rest.Payload;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/application-context.xml"})
public class RestClientServerTest {

    private static final String LOCK = "b2b88cec-d83a-4b3c-b4a8-b7217d5d7056";

    private ILock lock;

    @Autowired
    HazelcastInstance instance;

    @PostConstruct
    void accuireLock() {
        lock = instance.getLock(LOCK);
    }

    @Resource(name = "history")
    private IMap<String, Call> history;


    TestRule accuireLock = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            lock.lock();
        }

        @Override
        protected void after() {
            lock.unlock();
        }
    };

    TestRule clearHistory = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            history.clear();
        }

        @Override
        protected void after() {
            history.clear();
        }
    };

    @Rule
    public TestRule chain = RuleChain.outerRule(accuireLock).around(clearHistory);

    private String uuid = UUID.randomUUID().toString();

    private Call emptyCall = new Call().withRequest(
            new Call.Request().withMethod(Method.GET).withHeaders(
                    new ArrayList<Payload.Header>()).withAuth(new Call.Request.Auth()));

    private Call uniqCall = new Call().withId(uuid).withRequest(
            new Call.Request().withMethod(Method.GET).withHeaders(
                    new ArrayList<Payload.Header>()).withAuth(new Call.Request.Auth()));


    @Test
    public void retrieveCall() throws Exception {
        assertThat(new RestClientClient().call(), equalTo(emptyCall));
    }

    @Test
    public void retrieveCallWithIncorrectId() throws Exception {
        assertThat(new RestClientClient().call(uuid), nullValue());
    }

    @Test
    public void retrieveCallWithCorrectId() throws Exception {
        history.put(uuid, uniqCall);
        assertThat(new RestClientClient().call(uuid), equalTo(uniqCall));
    }

    @Test
    public void removeCallFromHistory() throws Exception {
        history.put(uuid, uniqCall);
        new RestClientClient().deleteCall(uuid);
        assertThat(history.get(uuid), nullValue());
    }

    @Test
    public void retrieveEmptyHistory() throws Exception {
        List calls = new RestClientClient().calls();
        assertThat(calls.isEmpty(), is(true));
    }

    @Test
    public void retrieveNonemptyHistory() throws Exception {
        history.put(uuid, uniqCall);
        List calls = new RestClientClient().calls();
        assertThat(calls.size(), equalTo(1));
        assertThat(calls.get(0), equalTo(uniqCall));
    }

    @Test
    public void request() throws Exception {
        Call.Request request = new Call.Request().withUrl("http://localhost:8080/api/call").withMethod(Method.GET)
                .withHeaders(new Payload.Header().withName("Accept").withValue("application/json"));
        new RestClientClient().request(request);
        assertThat(history.keySet().size(), is(1));
    }
}
