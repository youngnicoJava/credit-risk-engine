package com.creditrisk.infrastructure.config;
import com.creditrisk.domain.policy.BaselineCreditPolicy;
import com.creditrisk.domain.policy.CreditPolicy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.time.Clock;
@ApplicationScoped public class CoreConfiguration {@Produces @ApplicationScoped Clock clock(){return Clock.systemUTC();}@Produces @ApplicationScoped CreditPolicy policy(){return new BaselineCreditPolicy();}}
