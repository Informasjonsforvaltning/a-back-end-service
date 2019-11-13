FROM openjdk:11

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#VOLUME /tmp
#COPY . /app
#WORKDIR /app
ARG JAR_FILE
# RUN ls
RUN echo ${JAR_FILE}
ADD target/${JAR_FILE} app.jar
RUN sh -c 'touch /app.jar'
CMD java -jar $JAVA_OPTS app.jar