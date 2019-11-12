FROM openjdk:11-jre

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#WORKDIR .
COPY ./home/runner/work/a-backend-service/a-backend-service/target/* ./target
RUN ls
#VOLUME /tmp
ARG JAR_FILE
ADD target/*SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
CMD java -jar $JAVA_OPTS app.jar