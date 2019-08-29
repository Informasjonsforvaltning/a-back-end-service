# A template for a back-end service

## Requirements

- docker
- maven
- jdk (openjdk-8-jdk)

## To build and run locally

```
  mvn clean install
  docker-compose up
  curl -H "Accept: application/json" http://localhost:8080/template
```

## The API

A nice way to understand what this API does, check the [specification](./src/main/resources/specification/template.yaml)
