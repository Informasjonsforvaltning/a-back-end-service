# A template for a back-end service

## Requirements

- docker
- maven
- jdk (openjdk-8-jdk)

## To build and run locally

Set environment variables

```
export SONAR_LOGIN=<obtain-key-from-sonar>
export TEMPLATE_MONGO_USERNAME=<anything>
export TEMPLATE_MONGO_PASSWORD=<anything>
export TEMPLATE_MONGO_HOST=mongodb:27017
```

Or you can put the variables in a .env file in the root directory of your project.

```
$ cat .env
export SONAR_LOGIN=<obrain-key-from-sonar>
TEMPLATE_MONGO_USERNAME=anything
TEMPLATE_MONGO_PASSWORD=anything
```

## Build and run

```
  mvn clean install
  docker-compose up
  curl -H "Accept: application/json" http://localhost:8080/template
```

## The API

A nice way to understand what this API does, check the [specification](./src/main/resources/specification/a-backend-service.yaml)
