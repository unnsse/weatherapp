# Weather App - Overview

This codebase is intended as an example of how to obtain detailed weather information based on a US based mailing address (provided by user via HTTP endpoint or HTML form feeds located in `ReactJS` webapp). 

Behind-the-scenes uses the zip code to find the longitude and latitude coordinates to pull real-time weather information. Also, supports caching on the microservice side.

Comprises of a `ReactJS` based front end web app along with a `Spring Boot` Microservice backend. 

The `Spring Boot` Microservice needs to be built and running before `ReactJS` web app can be used.

Instructions for building and running are listed inside each of these two's individual `README.md` files.

ReactJS's `README.md` file is located [here](./frontend/README.md).
Spring Boot Microservice's `README.md` file is located [here](./backend/README.md).