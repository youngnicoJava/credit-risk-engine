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

Allowed dependency direction is inward only. `domain` has no Jakarta, Quarkus, Jackson, Hibernate, Kafka, REST or persistence dependencies. `application` defines use cases and ports around the domain; concrete use cases also use CDI and transactional annotations. Domain remains framework-independent. `adapter` translates transport/persistence representations and implements application ports. `infrastructure` wires framework configuration, clock, correlation and readiness. REST DTOs and Kafka schemas remain explicit and are not persistence entities.

Decision, explanation, and result outbox append run in one `@Transactional` use case. The scheduled publisher runs after commit. Kafka delivery is at-least-once; request ID and deterministic result event ID make retries safe. A request ID reused with materially different input is a conflict, not a replay.

The current policy makes eligibility, affordability, score components, risk band and reasons domain concepts. The installment used by CRE is a fixed 48% nominal annual risk stress estimate because LO's actual offer is priced only after approval; it does not make or reproduce a loan offer quote.

The analyst HTTP API provides read-only paginated assessment discovery and detail. Query criteria are modeled in the application layer and translated to parameterized Panache filters by the persistence adapter. The summary response intentionally avoids returning applicant financial details; detail remains role-protected and contains only the assessment evidence already retained by the domain.

The `frontend/` application is a separate Vite SPA. OIDC is handled by the browser with Authorization Code + PKCE and a public client; the API validates bearer tokens and realm roles. UI role checks are only navigation support. Local dev imports a demo realm, while production requires the real OIDC issuer and client configuration from the environment.
