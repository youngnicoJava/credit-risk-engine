package com.creditrisk.adapter.in.rest;
import java.time.Instant;
public record ApiError(String code,String message,Instant timestamp,String correlationId){static ApiError of(String c,String m){Object id=org.jboss.logging.MDC.get("correlationId");return new ApiError(c,m,Instant.now(),id==null?null:id.toString());}}
