# Architecture and dependency direction

```text
Frameworks / Infrastructure
    ↓ implements
Adapters (REST, Kafka, Panache)
    ↓ invoke
Application use cases and ports
    ↓ use
Domain model, policy, value objects
```

Allowed dependency direction is inward only. `domain` has no Jakarta, Quarkus, Jackson, Hibernate, Kafka, REST or persistence dependencies. `application` defines use cases and ports and depends on domain only. `adapter` translates transport/persistence representations and implements application ports. `infrastructure` wires framework configuration, clock, correlation and readiness. REST DTOs and Kafka schemas remain explicit and are not persistence entities.

Decision persistence and result outbox append run in one `@Transactional` application use case. The scheduled publisher runs only after commit. Kafka delivery is at-least-once; request ID and deterministic result event ID make retries safe.
