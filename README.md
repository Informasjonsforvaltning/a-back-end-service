# A template for a back-end service

## Requirements

- docker
- maven
- jdk (openjdk-8-jdk)

## To build and run locally

Set environment variables

```
export SONAR_LOGIN=<obtain-key-from-sonar>
export MONGO_USERNAME=<anything>
export MONGO_PASSWORD=<anything>
```

Or you can put the variables in a .env file in the root directory of your project.

```
$ cat .env
export SONAR_LOGIN=<obrain-key-from-sonar>
MONGO_USERNAME=anything
MONGO_PASSWORD=anything
```

## Build and run

```
  mvn clean install
  docker-compose up
  curl -H "Accept: application/json" http://localhost:8080/version
```

## The API

A nice way to understand what this API does, check the [specification](./src/main/resources/specification/a-backend-service.yaml)

dbup: "docker-compose -f docker-compose-mongodb.yml up -d mongodb ",
dbdown: "docker-compose -f docker-compose-mongodb.yml stop mongodb ",