# auth-starter

Spring Boot starter for authorization infrastructure (based on spring-security-starter)

todo:
- ApplicationEventPublisher vs ApplicationEventMulticaster for multiple domain events listeners ?
- event persistence for guaranteed delivery ?
- enforce async events only or allow synchronous events processing (including interfering with original transaction via `@TransactionalEventListener`) ?

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


---
###Additional principal data
Class `AuthenticatedUserDetails` contains field of type `Object` named `additionalUserData`.
By default this field is empty, but you can put there any additional data, for instance, first name and last name.
To achieve this you should define this bean:
```
@Bean
public AdditionalUserDataFetchHandler additionalUserDataFetchHandler() {
    return user -> myCustomUserDetails.getSomeSpecialData(user.id());
}
```