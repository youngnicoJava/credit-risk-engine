package com.creditrisk.adapter.in.rest;

import com.creditrisk.application.usecase.AssessmentRequestConflictException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AssessmentRequestConflictMapper implements ExceptionMapper<AssessmentRequestConflictException> {
    @Override public Response toResponse(AssessmentRequestConflictException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(ApiError.of("ASSESSMENT_REQUEST_ID_REUSED_WITH_DIFFERENT_INPUT", "The assessment request ID already belongs to a different financial profile."))
                .build();
    }
}
