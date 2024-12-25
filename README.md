# OAuth2 Client Demo

This project shows how to create an OAuth2 client  and consume an endpoint protected by OAuth2 authorization server using Spring Boot
## Get up and running

1. Clone the project locally

2. Ensure that you have:
   * java installed on your system (21)
  

3. Build application using the following:

``` ./gradlew clean build ``` 

4. Run application using the following

``` ./gradlew bootRun  ```



### Sample endpoint

```
    http://localhost:8080/transactions/de2da6c9-18be-48d4-8053-867ed90a316a

```

### Sample output

```
    {
      "status" : "ACCC"
    }
```
