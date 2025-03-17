# Weather App - Backend

This is a Spring Boot Microservice which displays weather information based on a specific US based address provided by the user.

## Test

To run unit and integration tests, issue the following maven command from inside the `backend` folder:

`mvn clean test`

## Build

While inside the `backend` folder, build (and run the tests):

`mvn clean install`

## Run Microservice

`mvn spring-boot:run`

### Test via Endpoint

POST http://localhost:8080/api/weather

Request Body:

```javascript
{
    "street": "One Apple Park Way",
    "city": "Cupertino",
    "state": "CA",
    "zipCode": "95014"
}
```

### Use via Frontend Web App

While backend microservice is running, cd into `frontend` folder and follow instructions inside its `README.md` (specifically, telling Node to use legacy OpenSSL provider before running `npm start`). 
