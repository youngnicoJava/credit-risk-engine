package com.creditrisk.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class OutboxPanacheRepository implements PanacheRepositoryBase<OutboxEntity, UUID> {}
