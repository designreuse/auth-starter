# auth-starter

Spring Boot starter for authorization infrasructure (based on spring-security-starter)

### Custom implementations  
Auth-starter gives you ability to redefine some controller-level methods.
You can override provided implementations by creating a bean of corresponding type in your app context.

```
@Bean
public CreateUserHandler createUserHandler() {
    return userRegistrationRequest -> {
        ...custom registration process
    }
}
```

Since redefined methods are controller-level, assumed that you return from them something like `ResponseEntity` etc. 

##### List of available customizable handlers
* `CreateUserHandler` 