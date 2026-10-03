package com.creditrisk.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidInputMapper implements ExceptionMapper<IllegalArgumentException> {
  public Response toResponse(IllegalArgumentException e) {
    return Response.status(400)
        .entity(
            ApiError.of(
                "INVALID_RISK_ASSESSMENT_REQUEST",
                "The request contains an unsupported or invalid value."))
        .build();
  }
}
