package com.creditrisk.domain.valueobject;
import java.util.Objects;
public record PolicyVersion(String value) { public PolicyVersion { Objects.requireNonNull(value); if(value.isBlank()||value.length()>40)throw new IllegalArgumentException("Invalid policy version"); } }
