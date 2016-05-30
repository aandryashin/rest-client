package com.github.aandryashin.server;

import org.apache.commons.lang.exception.ExceptionUtils;

public class HttpClientServiceException extends RuntimeException {
    public HttpClientServiceException(Throwable cause) {
        super(cause.getMessage() != null ? cause : ExceptionUtils.getRootCause(cause));
    }
}
