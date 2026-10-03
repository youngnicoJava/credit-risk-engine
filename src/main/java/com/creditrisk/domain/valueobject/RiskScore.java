package com.creditrisk.domain.valueobject;
public record RiskScore(int value) { public RiskScore { if(value<0||value>1000)throw new IllegalArgumentException("Risk score must be between 0 and 1000"); } }
