# auth-starter

Spring Boot starter for authorization infrastructure (based on spring-security-starter)

todo:
- ApplicationEventPublisher vs ApplicationEventMulticaster for multiple domain events listeners ?
- event persistence for guaranteed delivery ?
- enforce async events only or allow synchronous events processing (including interfering with original transaction via `@TransactionalEventListener`) ?
