# N26 Code Challenge

[![Build Status](https://travis-ci.org/arturbdr/code-challenge.svg?branch=master)](https://travis-ci.org/arturbdr/code-challenge)
[![codecov](https://codecov.io/gh/arturbdr/code-challenge/branch/master/graph/badge.svg)](https://codecov.io/gh/arturbdr/code-challenge)

### Project Structure
The project was developed using, as reference, Clean Architecture (from Uncle Bob)

Every communication coming to this app will reach one gateway. And, for this application, the gateway is a **Controller** ``TransactionController``.
Once the request reaches the controller, it will validate the incoming payload and convert it into a **Domain** object.
This converted domain object will be passed for the **UseCase**. The UseCase perform **business** rules and if no problem was detected it will call the inmemory **gateway** to store the data.
The implementation of an inmemory database was implemented in a **gateway** ``InMemoryTransactionStoreGatewayImpl``.
The concurrency control is done in this **gateway** too.

### Scheduler
In the usecase there is a Spring Scheduler configured to be executed at every second. The objective of this scheduler is to
remove old Transaction Metrics from the inMemory database (transactions with more than 60 seconds).
    
### Frameworks and libraries used
- Spring Boot version 2.1.3 - For exposing the RESTFull endpoint, Dependency Injection and all testing platform provided by Spring
- Lombok - To reduce Java verbosity using his annotations
- Swagger - To provide a documentation along with a friendly UI. It can be accessed in [Swagger](http://localhost:8080/swagger-ui.html) once the application's started
- FixtureFactory - Used in the tests to create templates of the objects.
- JaCoCo - Maven plugin to provide local test coverage. After every ``mvn clean test`` it will generate local report in the folder ``/target/site/jacoco/index.html``   

### Testing
There are two types of test of this application 
#### Unit Testing
- The usecase and the inMemory implementation was tested with unit tests.
It was not necessary to start all the spring context to test their functionalities and also, the unit test is faster without the spring context.
 
#### Integration Tests
- Controller is the unique layer tested without any mock and calling the real APIs. Spring Boot starts the context and server (tomcat) 
and afterwards the integration test begin consuming the APIs passing through all the application layers.

### Endpoint
There are two endpoint in this application and they were built following the challenge specs:
- /statistics --> endpoint to retrieve the calculated metrics. To test it using curl command ``curl -X GET "http://localhost:8080/statistics"``
If there is any metric, it will return an status code 200 along with the metric. 204 otherwise.

- /transactions --> endpoint to register a new metric. To test it ``curl -X POST "http://localhost:8080/transactions" -H "Content-Type: application/json;charset=UTF-8" -d "{ \"amount\": 12.3, \"timestamp\": 1478192204000}"``
If the transaction is not old (has not occurred more than 60 seconds ago) this endpoint will return 201. 204 otherwise.

### Metrics and Build
This app is integrated with:
- TravisCI - A continuous integration tool. It is being used in this application to check build status.<br/>
- Codecov - A tool that checks the quality and the test coverage of the application.<br/>
Both their badges are updated automatically at every push to github.

### How to start this project
``mvn spring-boot:run``
It will start in the port 8080. 

### Embedded Maven
If maven is not installed, there's and embedded maven in this application. To use it: ``./mvnw spring-boot:run`` for mac/unix SO or ``mvnw.cmd spring-boot:run`` for windows SO
It will start in the port 8080.