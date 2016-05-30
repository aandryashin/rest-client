package com.github.aandryashin.server;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

public class Application extends ResourceConfig {

    public Application() {
        this(new Class<?>[0]);
    }

    public Application(Class<?>... classes) {
        register(RequestContextFilter.class);
        register(JacksonFeature.class);
        register(MultiPartFeature.class);
        //register(LoggingFilter.class);
        register(classes);
    }

}
