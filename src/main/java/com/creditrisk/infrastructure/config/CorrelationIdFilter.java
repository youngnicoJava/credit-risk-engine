package com.creditrisk.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;
import org.jboss.logging.MDC;

@Provider
@ApplicationScoped
public class CorrelationIdFilter implements ContainerRequestFilter, ContainerResponseFilter {
  private static final String HEADER = "X-Correlation-ID";

  public void filter(ContainerRequestContext c) {
    String id = c.getHeaderString(HEADER);
    if (id == null || !id.matches("[A-Za-z0-9._-]{1,100}")) id = UUID.randomUUID().toString();
    c.setProperty(HEADER, id);
    MDC.put("correlationId", id);
  }

  public void filter(ContainerRequestContext r, ContainerResponseContext c) throws IOException {
    Object id = r.getProperty(HEADER);
    if (id != null) c.getHeaders().putSingle(HEADER, id.toString());
    MDC.remove("correlationId");
  }
}
