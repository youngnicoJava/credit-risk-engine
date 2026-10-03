package com.creditrisk.adapter.in.rest;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationErrorMapper implements ExceptionMapper<ConstraintViolationException> {
  public Response toResponse(ConstraintViolationException e) {
    return Response.status(400)
        .entity(
            ApiError.of(
                "INVALID_RISK_ASSESSMENT_REQUEST",
                "The request does not satisfy the API contract."))
        .build();
  }
}
