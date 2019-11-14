FROM openjdk:11-jre

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp
ARG JAR_FILE
EXPOSE 8080
RUN ls
RUN pwd
RUN sh -c 'touch /app.jar'
ADD target/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]