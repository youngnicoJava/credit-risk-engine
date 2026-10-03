package com.creditrisk.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundMapper implements ExceptionMapper<jakarta.ws.rs.NotFoundException> {
  public Response toResponse(jakarta.ws.rs.NotFoundException e) {
    return Response.status(404)
        .entity(
            ApiError.of(
                "RISK_ASSESSMENT_NOT_FOUND", "The requested risk assessment was not found."))
        .build();
  }
}
