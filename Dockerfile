FROM openjdk:11-jre

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp
ARG JAR_FILE
RUN ls
WORKDIR home/runner/work/a-backend-service/a-backend-service/
RUN ls
ADD target/*SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
CMD java -jar $JAVA_OPTS app.jar