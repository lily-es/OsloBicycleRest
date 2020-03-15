# OsloBicycleRest
Kotlin [Spring Boot](https://spring.io/projects/spring-boot) app that exposes data on available locks and bikes for 
[Oslo City Bike](https://oslobysykkel.no/en) on a REST endpoint. 
Run with Java 11

## How to run
Run the project with ```mvn spring-boot:run```

The data is available in JSON format at ```http://localhost:8080/info```

### Debug
To debug the project do ```mvn spring-boot:run@debug``` and attach a debugger to the process on port 5005

### Test
To test the project do ```mvn test```
