# auth-starter

Spring Boot starter for authorization infrastructure (based on spring-security-starter)

todo:
- ApplicationEventPublisher vs ApplicationEventMulticaster for multiple domain events listeners ?
- event persistence for guaranteed delivery ?
- enforce async events only or allow synchronous events processing (including interfering with original transaction via `@TransactionalEventListener`) ?

### Custom implementations  
Auth-starter gives you ability to hook into some scenarios.
You can override provided implementations by creating a bean of corresponding type in your app context.

```
@Bean
public PreProcessRegistrationStep preProcessRegistrationStep() {
    return userRegistrationRequest -> {
        ...custom processes before registration, 
        ...for instance custom validations
        return registrationRequestAfterPreProcessing;
    }
}
```

##### List of available hooks
* `PreProcessRegistrationStep` 


---
###Additional principal data
Class `AuthenticatedUserDetails` contains field of type `Object` named `additionalUserData`.
By default this field is empty, but you can put there any additional data, for instance, first name and last name.
To achieve this you should define this bean:
```
@Bean
public AdditionalUserDataFetchHandler additionalUserDataFetchHandler() {
    return user -> myCustomUserDao.getSomeSpecialData(user.id());
}
```